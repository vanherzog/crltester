package crltester.test.ocsp;

import android.os.AsyncTask;
import android.os.Build;

import com.example.andi.Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.CertificateStatus;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.RespID;
import org.bouncycastle.cert.ocsp.RevokedStatus;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.bouncycastle.cert.ocsp.UnknownStatus;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

import crltester.conf.CRLTester;
import crltester.test.CRLValidator;
import crltester.test.DateStatus;
import crltester.test.State;
import crltester.test.TestStatus;
import crltester.util.FileIO;
import crltester.util.URLQueryStringParser;
import de.bwi.pki.ocsp.IOcspCertificateValidator;
import de.bwi.pki.ocsp.OcspConnection;
import de.bwi.pki.ocsp.OcspProtocol;
import de.bwi.pki.ocsp.OcspRequestBuilder;
import de.bwi.pki.ocsp.OcspResponseValidator;

/**
 * Class performing OCSP-based CRL tests.
 * 
 * @author Benjamin Sanno @ IBM
 * @author Markus Dolze @ T-Systems
 * @author Marc Hoersken @ IBM
 * @version $Revision: 4.0 2015-02-16$
 */
public class OCSPTester extends AsyncTask<Server, Void, State> implements IOcspCertificateValidator {
	ArrayList<Server> serverList;

	public static final String ISSUER_CERT = "issuerCert";

	/**
	 * OCSP URL path parameter name for: Subject certificate
	 */
	public static final String SUBJECT_CERT = "subjectCert";

	/**
	 * OCSP URL path parameter name for: Subject certificate serial number
	 */
	public static final String SUBJECT_CERT_SERIAL = "subjectCertSerial";

	public static final String HASH_ALGORITHM = "hashAlgorithm";



	public OCSPTester(ArrayList<Server> serverList) {
		this.serverList = serverList;
	}

	/**
	 * Check CRL from OCSP-responder (HTTP webserver).
	 * 
	 * @param ocspUrlPath	OCSP-responder URL path.
	 * @return	OCSP certificate state.
	 */
	public OCSPCertState checkOcspUrlPath(String ocspUrlPath, String issuerCert, BigInteger subjectCertSerial, String ocspHashAlgorithm) {
		URL ocspUrl;
		try {
			ocspUrl = new URL(ocspUrlPath);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		Map<String, List<String>> ocspUrlParams;
		try {
			ocspUrlParams = URLQueryStringParser.getUrlParameters(ocspUrl.getRef());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}


		AlgorithmIdentifier hashAlgorithm = RespID.HASH_SHA1;
		if (!ocspHashAlgorithm.isEmpty() && OCSPHashAlgorithms.getDigests().containsKey(ocspHashAlgorithm)) {
			hashAlgorithm = AlgorithmIdentifier.getInstance(OCSPHashAlgorithms.getDigests().get(ocspHashAlgorithm));
		}

		if (issuerCert == null || (subjectCertSerial == null) || hashAlgorithm == null) {
			System.out.println("Configuration invalid.");
			return new OCSPCertState(null, TestStatus.CONFIGURATION_ERROR);
		}

		return this.checkOcspUrlPath(ocspUrl, issuerCert, subjectCertSerial, hashAlgorithm);
	}

	/**
	 * Check CRL from OCSP-responder (HTTP webserver).
	 * 
	 * @param ocspUrl			OCSP-responder URL.
	 * @param issuerCertPath	Path to the X.509 issuer certificate.
	 * @param subjectCertSerial	Serial number of the subject certificate.
	 * @param hashAlgorithm		OID of the OCSP hash algorithm.
	 * @return	OCSP certificate state.
	 */
	private OCSPCertState checkOcspUrlPath(URL ocspUrl, String issuerCertPath, BigInteger subjectCertSerial, AlgorithmIdentifier hashAlgorithm) {
		X509Certificate issuerCert = null;
		X509Certificate subjectCert = null;

		try {
			if (issuerCertPath != null && !issuerCertPath.isEmpty()) {
				issuerCert = this.loadCertificate(issuerCertPath);
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			return new OCSPCertState(null, TestStatus.CONFIGURATION_ERROR);
		}

		if (issuerCert == null) {
			System.out.println("Unable to load or find issuer certificate.");
			return new OCSPCertState(null, TestStatus.CONFIGURATION_ERROR);
		}

		String name = issuerCert.getSubjectDN().toString();

		if (subjectCert == null && subjectCertSerial == null) {
			System.out.println("Unable to load or find subject certificate or serial number.");
			return new OCSPCertState(name, TestStatus.CONFIGURATION_ERROR);
		}

		if (subjectCert != null) {
			return this.validateCertificate(ocspUrl, name, issuerCert, subjectCert, hashAlgorithm);
		} else {
			return this.validateCertificate(ocspUrl, name, issuerCert, subjectCertSerial, hashAlgorithm);
		}
	}

	/**
	 * Method to load X.509 certificates from the file-system.
	 * 
	 * @param certPath	Path to the X.509 certificate file.
	 * @return	X.509 certificate object.
	 * @throws IOException
	 * @throws CertificateException
	 */
	private X509Certificate loadCertificate(String certPath) throws IOException, CertificateException {
		System.out.println("Loading the certitificate " + certPath + " from file.");

		FileInputStream certInputStream = FileIO.getFileInputStream(certPath);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate cert = (X509Certificate) cf.generateCertificate(certInputStream);

		System.out.println("Certificate loaded successfully from file.");

		return cert;
	}

	/**
	 * Validate CRL by X.509 issuer and subject certificates.
	 * 
	 * @param responderURL			OCSP-responder URL.
	 * @param name					X.500 name of issuer certificate.
	 * @param issuerCertificate		X.509 issuer certificate.
	 * @param subjectCertificate	X.509 subject certificate.
	 * @param hashAlgorithm			OCSP hash algorithm.
	 * @return	OCSP certificate state.
	 */
	private OCSPCertState validateCertificate(URL responderURL, String name, Certificate issuerCertificate, Certificate subjectCertificate, AlgorithmIdentifier hashAlgorithm) {
		System.out.println("Converting subject certificate ...");
		X509CertificateHolder subjectCert;
		try {
			subjectCert = new X509CertificateHolder(subjectCertificate.getEncoded());
		} catch (CertificateEncodingException e) {
			System.out.println("Unable to encode X.509 certificate.");
			return new OCSPCertState(name, TestStatus.UNKNOWN_ERROR);
		} catch (IOException e) {
			System.out.println("Unable to decode X.509 certificate.");
			return new OCSPCertState(name, TestStatus.UNKNOWN_ERROR);
		}

		return this.validateCertificate(responderURL, name, issuerCertificate, subjectCert.getSerialNumber(), hashAlgorithm);
	}

	/**
	 * Validate CRL by X.509 issuer certificate and subject serial.
	 * 
	 * @param responderURL			OCSP-responder URL.
	 * @param name					X.500 name of issuer certificate.
	 * @param issuerCertificate		X.509 issuer certificate.
	 * @param subjectCertSerial		Subject certificate serial.
	 * @param hashAlgorithm			OCSP hash algorithm.
	 * @return	OCSP certificate state.
	 */
	private OCSPCertState validateCertificate(URL responderURL, String name, Certificate issuerCertificate, BigInteger subjectCertSerial, AlgorithmIdentifier hashAlgorithm) {
		System.out.println("Converting issuer certificate ...");
		X509CertificateHolder issuerCert;
		try {
			issuerCert = new X509CertificateHolder(issuerCertificate.getEncoded());
		} catch (CertificateEncodingException e) {
			System.out.println("Unable to encode X.509 certificate.");
			return new OCSPCertState(name, TestStatus.UNKNOWN_ERROR);
		} catch (IOException e) {
			System.out.println("Unable to decode X.509 certificate.");
			return new OCSPCertState(name, TestStatus.UNKNOWN_ERROR);
		}

		System.out.println("Creating CertificateID ...");
		CertificateID certID;
		try {
			JcaDigestCalculatorProviderBuilder providerBuilder = new JcaDigestCalculatorProviderBuilder();
			providerBuilder = providerBuilder.setProvider(BouncyCastleProvider.PROVIDER_NAME);

			DigestCalculatorProvider digestCalcProv = providerBuilder.build();
			DigestCalculator digestCalculator = digestCalcProv.get(hashAlgorithm);

			certID = new CertificateID(digestCalculator, issuerCert, subjectCertSerial);
		} catch (OperatorCreationException e) {
			System.out.println("Unable to setup DigestCalculator.");
			return new OCSPCertState(name, TestStatus.CONFIGURATION_ERROR);
		} catch (OCSPException e) {
			System.out.println("Unable to setup CertificateID.");
			return new OCSPCertState(name, TestStatus.CONFIGURATION_ERROR);
		}

		return this.validateCertificateID(responderURL, name, certID, hashAlgorithm);
	}

	/**
	 * Validate CRL by X.509 issuer certificate and subject serial.
	 * 
	 * @param responderURL			OCSP-responder URL.
	 * @param name					X.500 name of issuer certificate.
	 * @param certificateID			OCSP certificate ID.
	 * @param hashAlgorithm			OCSP hash algorithm.
	 * @return	OCSP certificate state.
	 */
	private OCSPCertState validateCertificateID(URL responderURL, String name, CertificateID certificateID, AlgorithmIdentifier hashAlgorithm) {
		OcspRequestBuilder builder = new OcspRequestBuilder(hashAlgorithm);
		OcspResponseValidator validator = new OcspResponseValidator(this);

		System.out.println("Creating OCSP request ...");
		OCSPReq ocspRequest;
		try {
			ocspRequest = builder.buildRequest(certificateID);
		} catch (OCSPException e) {
			System.out.println("Unable to generate OCSP request.");
			return new OCSPCertState(name, TestStatus.UNKNOWN_ERROR);
		}

		System.out.println("Encoding OCSP request packet ...");
		byte[] ocspRequestEncoded;
		try {
			ocspRequestEncoded = ocspRequest.getEncoded();
		} catch (IOException e) {
			System.out.println("Unable to encode OCSP request packet.");
			return new OCSPCertState(name, TestStatus.UNKNOWN_ERROR);
		}

		System.out.println("Connecting to OCSP responder ...");
		OcspConnection ocspConnection;
		try {
			ocspConnection = this.getConnection(responderURL);
		} catch (Exception e) {
			System.out.println("Unable to open OCSP connection.");
			return new OCSPCertState(name, TestStatus.CONNECTION_ERROR);
		}

		System.out.println("Sending OCSP request ...");
		try {
			ocspConnection.sendRequest(ocspRequestEncoded);
		} catch (IOException e) {
			System.out.println("Unable to send OCSP request.");
			return new OCSPCertState(name, TestStatus.CONNECTION_ERROR);
		}

		System.out.println("Receiving OCSP response ...");
		byte[] ocspResponseEncoded;
		try {
			ocspResponseEncoded = ocspConnection.receiveResponse();
		} catch (IOException e) {
			System.out.println("Unable to receive OCSP request.");
			return new OCSPCertState(name, TestStatus.CONNECTION_ERROR);
		}

		System.out.println("Decoding OCSP response packet ...");
		OCSPResp ocspResponse;
		try {
			ocspResponse = new OCSPResp(ocspResponseEncoded);
		} catch (IOException e) {
			System.out.println("Unable to decode OCSP response packet.");
			return new OCSPCertState(name, TestStatus.INVALID_RESPONSE_ERROR);
		}

		System.out.println("Checking OCSP response status ...");
		int ocspStatus = ocspResponse.getStatus();
		if (ocspStatus != 0) {
			if (ocspStatus > 0 && ocspStatus < OcspProtocol.responseStatusStrings.length) {
				System.out.println("OCSP responder returned error '" + OcspProtocol.responseStatusStrings[ocspStatus] + "'.");
			} else {
				System.out.println("OCSP responder returned unknown error'.");
			}
			return new OCSPCertState(name, TestStatus.INVALID_RESPONSE_ERROR);
		}

		BasicOCSPResp basicOcspResponse;
		try {
			basicOcspResponse = (BasicOCSPResp) ocspResponse.getResponseObject();
			System.out.println("Response version: " + basicOcspResponse.getVersion());
			System.out.println("Response produced at: " + basicOcspResponse.getProducedAt());
		} catch (OCSPException e) {
			System.out.println(e.toString());
			return new OCSPCertState(name, TestStatus.INVALID_RESPONSE_ERROR);
		}

		boolean valid = validator.validateResponse(ocspResponse);
		if (!valid) {
			System.out.println("OCSP response signature invalid.");
			return new OCSPCertState(name, TestStatus.INVALID_RESPONSE_ERROR);
		}

		System.out.println("Compare nonce of OCSP request and response ...");
		Extension requestNonce = ocspRequest.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);
		Extension responseNonce = validator.getBasicResponse().getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);
		if (!requestNonce.getExtnValue().equals(responseNonce.getExtnValue())) {
			System.out.println("OCSP response nonce invalid.");
			return new OCSPCertState(name, TestStatus.INVALID_RESPONSE_ERROR);
		}

		System.out.println("Response signature verification passed.");

		SingleResp singleResp = validator.getSingleResponse(certificateID);
		if (singleResp == null) {
			System.out.println("No OCSP response for certificate found.");
			return new OCSPCertState(name, TestStatus.NO_RESULTS_WARNING);
		}

		return this.validateOcspResponse(name, singleResp);
	}

	/**
	 * Validate OCSP response from OCSP-based CRL-DP (OCSP responder).
	 *
	 * @param name				Common name.
	 * @param singleResponse	OCSP response.
	 * @return	OCSP certificate state.
	 */
	private OCSPCertState validateOcspResponse(String name, SingleResp singleResponse) {
		Set<TestStatus> testStates = new HashSet<TestStatus>(); 
		CertificateStatus certStatus = (CertificateStatus) singleResponse.getCertStatus();
		Date thisUpdate = singleResponse.getThisUpdate();
		Date nextUpdate = singleResponse.getNextUpdate();

		System.out.println("Verification result for issuer:\n - '" + name + "'");
		System.out.println("Revocation information for certificate serial: " + singleResponse.getCertID().getSerialNumber() + ".");

		if (certStatus == CertificateStatus.GOOD) {
			System.out.println("Certificate status: good");
		} else if (certStatus instanceof UnknownStatus) {
			testStates.add(TestStatus.CERTIFICATE_UNKNOWN);
			System.out.println("Certificate status: unknown");
		} else if (certStatus instanceof RevokedStatus) {
			testStates.add(TestStatus.CERTIFICATE_REVOKED);
			System.out.println("Certificate status: revoked");
			System.out.println("Revocation time: " + ((RevokedStatus) certStatus).getRevocationTime());
			RevokedStatus revokedStatus = (RevokedStatus) certStatus;
			if (revokedStatus.hasRevocationReason()) {
				int revocationReason = revokedStatus.getRevocationReason();
				if (revocationReason >= 0 && revocationReason < OcspProtocol.revocationReasonString.length) {
					System.out.println("Revocation reason: " + OcspProtocol.revocationReasonString[revocationReason]);
				} else {
					System.out.println("Unknown revocation reason: " + revocationReason);
				}
			}
		}

		DateStatus dateStatus = DateStatus.RED;
		if (thisUpdate != null && nextUpdate != null) {
			//18000000 sind 5h
			long warningTimespanMilliseconds = 18000000;
			dateStatus = CRLValidator.validateExpiration(thisUpdate, nextUpdate, warningTimespanMilliseconds);
		} else {
			testStates.add(TestStatus.INCOMPLETE_RESPONSE_ERROR);
		}

		return new OCSPCertState(name, thisUpdate, nextUpdate, dateStatus, certStatus, testStates);
	}

	/**
	 * Method to validate certificates.
	 * 
	 * @param certificate	X.509 certificate to be validated
	 * @return	Certificate state based upon OCSP-state values
	 */
	public CertificateStatus validateCertificate(Certificate certificate) {
		// Until OCSP-responders conform to RFC 6960, no responder certificate validation is performed.
		return CertificateStatus.GOOD;
	}
	
	private final OcspConnection getConnection(URL responderURL) throws IOException, InterruptedException{
      int retry = 0;
      OcspConnection connection;
      IOException exception = null;
      while(retry < 4) {
		  try {
			  connection = new OcspConnection(responderURL, CRLTester.USER_AGENT);
			  return connection;
		  } catch (IOException e) {
			  if (exception != null) {
				  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					  e.addSuppressed(exception);
				  }
			  }
			  exception = e;
		  }
		  retry++;
	  }
      throw exception;
  }


	@Override
	public State doInBackground(Server... server) {
		Server ocspServer = server[0];
		return checkOcspUrlPath(ocspServer.getLink(),ocspServer.getCert(), ocspServer.getSeriennummer(), ocspServer.getHash());
	}
}
