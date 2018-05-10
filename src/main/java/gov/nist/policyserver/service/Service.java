package gov.nist.policyserver.service;

import gov.nist.policyserver.access.PmAccess;
import gov.nist.policyserver.exceptions.ConfigurationException;
import gov.nist.policyserver.exceptions.InvalidPropertyException;
import gov.nist.policyserver.exceptions.SessionDoesNotExistException;
import gov.nist.policyserver.exceptions.SessionUserNotFoundException;
import gov.nist.policyserver.graph.PmGraph;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.graph.nodes.NodeType;
import gov.nist.policyserver.model.graph.nodes.Property;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;

import static gov.nist.policyserver.common.Constants.*;
import static gov.nist.policyserver.dao.DAO.getDao;

public class Service {
    public PmGraph graph;
    public PmAccess access;

    public Service() throws ConfigurationException {
        graph = getDao().getGraph();
        access = getDao().getAccess();
    }

    public Node getSessionUser(String session) throws SessionUserNotFoundException, SessionDoesNotExistException {
        HashSet<Node> sessions = graph.getNodesOfType(NodeType.S);
        for(Node node : sessions) {
            if(node.getName().equals(session)) {
                //get the user that is assigned to this node
                HashSet<Node> children = graph.getChildren(node);
                if(children.isEmpty()) {
                    throw new SessionUserNotFoundException(session);
                }

                return children.iterator().next();
            }
        }

        throw new SessionDoesNotExistException(session);
    }

    public Node getConnector() throws InvalidPropertyException, ConfigurationException {
        HashSet<Node> nodes = graph.getNodes();
        for(Node node : nodes) {
            if(node.getName().equals(CONNECTOR_NAME) && node.hasProperty(new Property(NAMESPACE_PROPERTY, CONNECTOR_NAMESPACE))) {
                return node;
            }
        }

        throw new ConfigurationException("Could not find connector node 'PM'.  Make sure to load super.pm first");
    }

    public static String generatePasswordHash(String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        int iterations = 100;
        char[] chars = password.toCharArray();
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + toHex(salt) + toHex(hash);
    }

    public static boolean checkPasswordHash(String stored, String toCheck) throws NoSuchAlgorithmException, InvalidKeySpecException{
        String part0 = stored.substring(0, 3);
        String part1 = stored.substring(3, 35);
        String part2 = stored.substring(35);
        int iterations = Integer.parseInt(part0);
        byte[] salt = fromHex(part1);
        byte[] hash = fromHex(part2);

        PBEKeySpec spec = new PBEKeySpec(toCheck.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        String x = toHex(testHash);

        int diff = hash.length ^ testHash.length;
        for(int i = 0; i < hash.length && i < testHash.length; i++)
        {
            diff |= hash[i] ^ testHash[i];
            if(hash[i] != testHash[i]){
                int cx = 0;
            }
        }
        return diff == 0;
    }

    private static byte[] fromHex(String hex)
    {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    private static String toHex(byte[] array)
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
        {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }
}
