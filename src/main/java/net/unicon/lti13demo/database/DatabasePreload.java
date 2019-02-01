/**
 * Copyright 2019 Unicon (R)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.unicon.lti13demo.database;

import net.unicon.lti13demo.config.ApplicationConfig;
import net.unicon.lti13demo.model.LtiUserEntity;
import net.unicon.lti13demo.model.PlatformDeployment;
import net.unicon.lti13demo.model.RSAKeyEntity;
import net.unicon.lti13demo.repository.LtiUserRepository;
import net.unicon.lti13demo.repository.PlatformDeploymentRepository;
import net.unicon.lti13demo.repository.RSAKeyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Check if the database has initial data in it,
 * if it is empty on startup then we populate it with some initial data
 */
@Component
@Profile("!testing")
// only load this when running the application (not for unit tests which have the 'testing' profile active)
public class DatabasePreload {

    static final Logger log = LoggerFactory.getLogger(DatabasePreload.class);

    @Autowired
    ApplicationConfig applicationConfig;

    @Autowired
    @SuppressWarnings({"SpringJavaAutowiredMembersInspection", "SpringJavaAutowiringInspection"})
    RSAKeyRepository rsaKeyRepository;
    @Autowired
    @SuppressWarnings({"SpringJavaAutowiredMembersInspection", "SpringJavaAutowiringInspection"})
    LtiUserRepository ltiUserRepository;
    @Autowired
    @SuppressWarnings({"SpringJavaAutowiredMembersInspection", "SpringJavaAutowiringInspection"})
    PlatformDeploymentRepository platformDeploymentRepository;

    @Value("${oicd.privatekey}")
    private String ownPrivateKey;
    @Value("${oicd.publickey}")
    private String ownPublicKey;


    @PostConstruct
    public void init() {

        if (platformDeploymentRepository.count() > 0) {
            // done, no preloading
            log.info("INIT - no preload");
        } else {
            // preload the sample data
            log.info("INIT - preloaded keys and user");
            // create our sample key

            rsaKeyRepository.saveAndFlush(new RSAKeyEntity("OWNKEY", true,
                    getOwnPublicKey(),
                    getOwnPrivateKey()));

            PlatformDeployment iss1 = new PlatformDeployment();
            iss1.setClientId("Ddbo123456");
            iss1.setIss("https://sakai.org");
            String iss2PublicKey = "-----BEGIN PUBLIC KEY-----" +
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwuvy1UpBbEzUF0C56CoA" +
                    "m14BuBpUJGrJTTpSLbi4rS0xnUgAohkri9CRexbjpPNjbAYaSi4/171T2eHlfAi4" +
                    "Qsv33jEdWgL8HfqFLqN09rHpxhBqWA8sFTARWgA1k7Ti/VeGclx41asCNxUnv0W+" +
                    "mDeyOBSiox6cyx04LZlxs0MkmGBP1Xf4Saq8wGaBI/lUwY52aGtveMkvH/xN8DNQ" +
                    "dk7Li9Q0tj3MCtpI7LE2c2h95Zl/DndDNrRAdHYgOdZg9EQcfiuWdRtUxufkdMoZ" +
                    "mVoYDo7H96tulDMudC0JB0MvaOnnb+MU9jIVuvQkvrZ0jhGmTx8K0gvz2QAgWw6/" +
                    "mwIDAQAB" +
                    "-----END PUBLIC KEY-----";
            iss1.setOidcEndpoint("https://lti-ri.imsglobal.org/platforms/89/authorizations/new");
            iss1.setDeploymentId("0002");
            iss1.setToolKid("hf8Sisblt0zj0KhjY8oAIH0ylU2PuYwnegc8Y9vJq9g");
            iss1.setPlatformKid("hf8Sisblt0zj0KhjY8oAIH0ylU2PuYwnegc8Y9vJq9g");
            String tool2PrivateString = "-----BEGIN PRIVATE KEY-----" +
                    "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC2QJCkV2gFoQD2" +
                    "z7dQRq7g5qIxPaZJJZAJ07wPxdAJiyuWbo0bMOvH//5IqmOnUdal7iNYtDKwr9Cx" +
                    "6UMqI34q6b080GPypyl058vR7Z31ZNv9d4csp81DJxW9UcSkuqTWbEDRvoHUXJLt" +
                    "mhO+CokQSkS2oM1mWQeV1r3T73zPUsUq/QinK8SgGamFb+TbWQOIbqCymKwnt0no" +
                    "2Vg7bqfelkAWXAIMBo9WoiNDiT7v3Ns7Fu8NJ64stXSYC8zzmTWbiUkx06SbJOKz" +
                    "H0HC9NqZmeOwcDyStXYt6mVJg+bOtpctuikCZIkYeJuEwWkR9LqvAdoCs1kVnq8k" +
                    "F1alNd35AgMBAAECggEALBamZvs2ENaIEyzgnazbtVBVwC+3wE4z8Aymm/Iwh36B" +
                    "Rtzrib5l63YEH7QIc3uav31CU70T3iZKCB/zvYfkh6EPxFxtMVA6+Srx5ZDj+28w" +
                    "wLpfmu/k+e/ElI3pUihMpAqAC71YTvUuHgh96iVGTwiIYt23kqDK8vaF6XUv7j8h" +
                    "D1do+4eX9oZM03dqh2cZfC1z+xdhiEQzEOSu7qcNhml6d/rpS0EkILnmBekA1adw" +
                    "UuaS/FQzcbggScSGtL2WL6CFB1gl82IGhJALqRASfRGWlkmlnTQ1fzYZdLLvWKlG" +
                    "MM1mWu3zmOGxNSKQwpEHlxDpSxemFAf7RkgavA5EeQKBgQDihvyG1Ba9xtW9jO80" +
                    "BPCpvyCmpX0SlhlP7kYKtZHqkEKd+SOvfzN8fxi/5BNRXnMmJFN3Mkc2sYssMzTx" +
                    "MABii2e6r02AwkLUBu2DX5O/qauCbVlhr1LtvMbKTw6jnJYpGkZMqnTTS/933DPD" +
                    "8xa8AsckFMsXiGRs9OpFpOF+cwKBgQDN9uUVbarh3o6xx4pABNp3QDLQeqllUlsr" +
                    "Z4JqX26MELE1hA5qaccaLMtSY5Pq8Qh36tQJhZFAYz3isxvEhhIkAZZKmKi9MKDK" +
                    "lf+u7vYWfpNYxUPwpB9ZRM4UCcquY24/FgKucorQI0KwYqOTJX2whKDBjiurINA2" +
                    "x658s5TK4wKBgAQqQThla+mfX0y166wELzyfxATsZAlUczCyC92kiwNKFb971jki" +
                    "2JqAZ78XfXdwiiN4ZYR6iy6pQwrUAjQxEsC9GXIoSP+GEt59Jh7VQg0zHHEwe4U9" +
                    "SQQQBYOwwm8lsOkej45XUACWlCLrDJScwp1AW9MBAt7y5g3OzwPqzS6bAoGAFoVO" +
                    "mz84liX9uFa3OTTOpodwhvdCmn+c1GwnCHaS4eHZXp6n7N7QFH6dZM7al6/vWx1k" +
                    "Pf5K2Z2AYM9w09ZNGX7K7jEvEjDFBCHOqVQbuG3yspwvR5rKirpJRkujy9m3blJ7" +
                    "zJNdtlCEtEC03hwVWD3ITiG7iKS336WJ4LzKIj0CgYBhhcvs9rnEx0pbMPyw3eK+" +
                    "v2utJ02u3MsWmynJbvjqTSwZhRfBlDA2uzOLvPUNNOWiGjExCrAe+fFkuO8l72wu" +
                    "T8RzsVTPwN9uKZOlm/sHd7KtETaMXRM94mT/uisQ9QahX48tw/c4miu+Sv2xWwQ1" +
                    "sNJ4OXzO/tir0uLgMp6XcA==" +
                    "-----END PRIVATE KEY-----";
            String tool2PublicString ="-----BEGIN PUBLIC KEY-----" +
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtkCQpFdoBaEA9s+3UEau" +
                    "4OaiMT2mSSWQCdO8D8XQCYsrlm6NGzDrx//+SKpjp1HWpe4jWLQysK/QselDKiN+" +
                    "Kum9PNBj8qcpdOfL0e2d9WTb/XeHLKfNQycVvVHEpLqk1mxA0b6B1FyS7ZoTvgqJ" +
                    "EEpEtqDNZlkHlda90+98z1LFKv0IpyvEoBmphW/k21kDiG6gspisJ7dJ6NlYO26n" +
                    "3pZAFlwCDAaPVqIjQ4k+79zbOxbvDSeuLLV0mAvM85k1m4lJMdOkmyTisx9BwvTa" +
                    "mZnjsHA8krV2LeplSYPmzraXLbopAmSJGHibhMFpEfS6rwHaArNZFZ6vJBdWpTXd" +
                    "+QIDAQAB" +
                    "-----END PUBLIC KEY-----";
            rsaKeyRepository.saveAndFlush(new RSAKeyEntity("hf8Sisblt0zj0KhjY8oAIH0ylU2PuYwnegc8Y9vJq9g",true, tool2PublicString,tool2PrivateString));
            rsaKeyRepository.saveAndFlush(new RSAKeyEntity("hf8Sisblt0zj0KhjY8oAIH0ylU2PuYwnegc8Y9vJq9g",false, iss2PublicKey,null));

            platformDeploymentRepository.saveAndFlush(iss1);

            PlatformDeployment iss2 = new PlatformDeployment();
            iss2.setClientId("imstestuser");
            iss2.setIss("ltiadv-cert.imsglobal.org");
            iss2.setJwksEndpoint("https://oauth2server.imsglobal.org/jwks");
            String iss3PublicKey = "";
            iss2.setOidcEndpoint("https://ltiadvantagevalidator.imsglobal.org/ltitool/oidcauthurl.html");
            iss2.setoAuth2TokenUrl("https://oauth2server.imsglobal.org/oauth2server/authcodejwt");
            iss2.setDeploymentId("testdeploy");
            iss2.setToolKid("imstester_4");
            iss2.setPlatformKid("imstester_4");
            String tool3PrivateString = "-----BEGIN RSA PRIVATE KEY-----" +
                    "MIIEowIBAAKCAQEAsW3eobPIj5LsyHcMGckVSSC621uL+0zkeMoWfXfNmvTH+zt5" +
                    "WOeEIdz+X7fK+F+lO7ic5WdJEGmp9/cjAf0Z6SsmnvvHlHV/xsWtJm4DiuuF2MAa" +
                    "hRQ5QEkhaEdh5QM2vAYyc8Nfxe504vA3czuynrW9MsOdZHeVzF+zWhhEl+olC5fW" +
                    "A1rhTUPpdxuZ0opVIrGJtI/QYfndoN+7zTs/4CXqG6WpB+AZio8j7c6fJLC7J33c" +
                    "pxB1+O+64Qbh+5sxz46cEByboAB8qerYCmcfxxfBbwyySBBK5X77aNHWA01B1kpO" +
                    "Q2VB8YKQk+OrXsPgJobPkR9ONWa9DC9JjEdUJwIDAQABAoIBAQCAA+qutt2NIY/v" +
                    "71zuudO+yHupSzsLXOY3dG+XpTnWhKhJTxb1m00Ndbqe6yfp3nCET2X8anIgAmzc" +
                    "+RXsGGZ6gmTCLp1IMyK3EuckJBowQFB5G9nGjNnl1R3idCZgqtnx/XKnbZ6LW8o/" +
                    "9tu7K6ZrtmrE1riXxWRyadYoufu7ssNTqtj03oh3Tvw+Ze6xvF6hpaxnbVHxJcGt" +
                    "xZO51L6rGOSFq5CJ81BswyBDOKB/Z2OC0o3m2t4ZF4/2Lf070sB7RoejGD7mhYVe" +
                    "lEOoC95C14hfcspzmDEb8I/n0MvAxlwddM4KZRilAJ+e2R0rM9M1MnyYsmYUsMNX" +
                    "EKWcx+/5AoGBAOLtNVbIohpY5kbX4WREJ/0INPbbx0gf68ozEZTjsOzIP7oaIzry" +
                    "URmxyZzSpx446QCO8s26vuxrPGm7OAteNS7UpDdunzKsaIlZScZQEpE9htp3MKKw" +
                    "KXaA4l7H55uWWnaUAcDqjEdybhYL6SbPKhOaK53VeHOLro900FiRnfaDAoGBAMgp" +
                    "O8GwAI3LbD06Fn+DT+3hj/i8wxbWilgJlI+RU+wWfQ421jMKv2dck8zbnzKGxEwA" +
                    "3WPh6gGMlkavEZ95d0qZ/TOkSh+VIjJuOrjcckRcrKcycYJJUzreO7ENsFbA+8xL" +
                    "Qp2gNV+NntiChzSUGY5Nup3keoaT9iV13oYDSdqNAoGARDn9Z3I7CqDf2zzcz0CO" +
                    "pUzqX64EZHL0eX6RMqqibw5l2pYxMW/ZYlhJvZS4GiYSJ9DSv3f+Hya+qytW1lQk" +
                    "uUfFd8USqDGd3G2z+KPqcTCGcviS7tb4IGDvrn976xNxb2VggZgDRRfqcUZzeu+e" +
                    "PvaDVpjv9g1xFkCQw5BEZfECgYBcSB5jywhGV14c0FYlDd5g9xiQfj6XnewEcM5M" +
                    "bp05gJjBX+jbeX4LYnRGA49fFSEVRWTMsxBXDIEQL5C5bJ/iBiLllz4RV4l/pLBw" +
                    "IDqSaAO1xhztC29S+bidhYkiRjEQ3DXnREC3QCzW9z7sr8ckg5OhTgBrYXYfiTtB" +
                    "n+yB1QKBgG/J+WhkqMEtZ8CgdoiTIqYKmFsLvl07wETAVU6Nv1sEI+jnhyug0QtQ" +
                    "yLAlBOVyrXuJ1DZMX6hTRij4L0jvnJFSq0Sv8COuLIH90xdq/NTNQ3LAy60l/3b1" +
                    "ojAnnRJORDegdJjCBxJ59Fch6Qfd+e8742DVsJu8zVo2garUVMH3" +
                    "-----END RSA PRIVATE KEY-----";
            String tool3PublicKey ="";
            rsaKeyRepository.saveAndFlush(new RSAKeyEntity("imstester_4",true, tool3PublicKey,tool3PrivateString));
            rsaKeyRepository.saveAndFlush(new RSAKeyEntity("imstester_4",false, iss3PublicKey,null));
            platformDeploymentRepository.saveAndFlush(iss2);

            PlatformDeployment iss4 = new PlatformDeployment();
            iss4.setClientId("dmccallum-platform-2-client-1");
            iss4.setIss("https://dmp2-lti-ri.imsglobal.org");
            iss4.setDeploymentId("idontknowwhattosetthisto");
            String iss4PublicKey = "-----BEGIN PUBLIC KEY-----" +
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2iSH+X2ZfSPhn9C3KwLF" +
                    "Ba5rzFvifgmSWl8f1cdrnVCjeYgiPBVC/5N54GPDHw0t8mC2pqr/g4t+H0oIAGXC" +
                    "Cl9SYrRsDN41g7iPXwWDbC6mFbzKZzsR4vhEGq434bUYS0uTS/HCqyp3J/YjY41X" +
                    "FoYtyr7H1z+bXyAc8Y6X6RZSxJMKk1b5xN5cPnipaXh8pjomDy3fqWA1K+3i9uge" +
                    "qduwxZx8AppinQRqlk/0hXA8DCuEdoESCcf5ZDIt8+m0lLOQJ68jqbd3Y6nO6vDy" +
                    "IZfWeXbG+Lztm6+WfVf1puWddcGUWtwzQyDj/br3bSVZW48bxSgC3HcSWUwb6LeS" +
                    "HwIDAQAB" +
                    "-----END PUBLIC KEY-----";
            iss4.setOidcEndpoint("https://lti-ri.imsglobal.org/platforms/110/authorizations/new");
            String tool4PrivateString = "-----BEGIN RSA PRIVATE KEY-----" +
                    "MIIEpAIBAAKCAQEA94DLZjdUAXryMMmUrz8L7CSMnH7nIKLQvWTSwlcSNbXoCXEi" +
                    "z7URUd8u5aNrorE6WwrAeUk5PK5oNZ1iBuZ8qQpMHw/AIH3G4i3m0y8JohcJBPtA" +
                    "jNNPLrvRpMeRMnxdPkA6TcSX1j9IkC4qA5AckzF1qtZl4Y7Jsbxh8e1Vz0fAwUIV" +
                    "bn6HQQqtvLHCOhswBJ28RXm0hll+ELSsY4T57S8WQOJM8FF/zB05D2Km5bklORSR" +
                    "KkAGy8ax8FUrvdXsqsiz0IQ/GYPNzfifx8VAPWCb8DXdM6Kn9l8IOYfzLQZkh91i" +
                    "/LfBvJ6uBR+SqROZ0/Vw30j+fuDzm3oB1FLihwIDAQABAoIBAQDGqT6aITbwqTrV" +
                    "JjiOIdD0DrI+uy3R6F4cZ100WKdpLUW+rmz+w5vPV5FRCcdPo2nvwcWUlM24g1Vq" +
                    "E5WnbEILfRR0qjPQ4KRO0AFC6bKVxF+c+/9oCgS4wlEYLofBOkmtuegSnAY0Fj+s" +
                    "WyGmEnqdO0nzgnvsfKwwWcFUauhBowZXTtqIn1XZZrUWzyq8m5Igb8juTZ4guZtn" +
                    "DTszLiFfap3uj2jz5LHwuk3VdJtzxowI9Vdu0TvMkb8LTTMRf6x4gUTauNFeeMri" +
                    "0BbPLL8Y0yKTjU1OIqHR6uj20fM3l3pwlsOq4wksdj4V8k0eBkvav1d3RF4XTiqj" +
                    "y1z0Q6eBAoGBAP9UCl82gxhhpPZ6vA3E44Wq7a0bGrvrltoF3I5iNCJfInDvdrvn" +
                    "jpPS0aXW/OqBbnO5V6d3ntz7QkWip//SG/+JDtVYiti0l6OTOX4UNwx6OB9Fot9k" +
                    "Z/0TQ7XxpgaV8T0qwTLZQIwd2y4UXTZhHZcCgSgvI9LwUkYqh1iEtow3AoGBAPgn" +
                    "e9+bOzE9M8XekGotC4WyCRdKzwkyowvpKiMGgb0IzaWGxT/sBXLFM9VUJPKuXtKE" +
                    "HHN6kEr55RgWaSUnJKtaxeR71wC0zSEVEKXwWb/LZqU9gXK+5bedh+3EtH/nIk2T" +
                    "VkscU++4MHqHJzgrciNIYI3MwuXtiBuhO9nDoFQxAoGABtJhAJA9Sm55cNhwo9GJ" +
                    "3q1lckWSHkk6G5MihG9dQznVerz7KCQUrCBq14p58W4J3G+sRt4sUW0pJSEmafBc" +
                    "LSSlT8/wzb9tecJaO+MgYWX2j+dVZbCSErHsEjVloTFqY5770Hb7gYLes0l6ABTy" +
                    "LHG1r7Qqud0P5a4HNbc+BVsCgYA6Ij3Rx3LZeSX8z8PzK7RMUMXyb/MKi5NIwgm+" +
                    "BNM17q8GfQkOjYNFPM+ExV6Wf6T1Gj/0CZ1uAxbZSr69FmtvvSco/J4Eq1Z2zpGE" +
                    "3kaYONVGFp+RuwjjuALU/8TtRb9yfvazR+KeAP9SZQEaFot5moYMEhECfl/pYR89" +
                    "R6fysQKBgQCN+FYL8waZBjfYEPVRjSxiSiUW6uyc/GcoR8jWa/t7pThjBCcX9b72" +
                    "DpxsGjKNJWmxv5rwdS2PG/gQ5AEr1O7dVtBPlxOf15aq4ObirRntvpAZn2gtPSmM" +
                    "qTHAXU6gVX3kn3sCWGRwuLWo7H7YJyt7DjSIkmpZIPxHSVyu1iA1Gg==" +
                    "-----END RSA PRIVATE KEY-----";
            String tool4PublicString = "-----BEGIN PUBLIC KEY-----" +
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA94DLZjdUAXryMMmUrz8L" +
                    "7CSMnH7nIKLQvWTSwlcSNbXoCXEiz7URUd8u5aNrorE6WwrAeUk5PK5oNZ1iBuZ8" +
                    "qQpMHw/AIH3G4i3m0y8JohcJBPtAjNNPLrvRpMeRMnxdPkA6TcSX1j9IkC4qA5Ac" +
                    "kzF1qtZl4Y7Jsbxh8e1Vz0fAwUIVbn6HQQqtvLHCOhswBJ28RXm0hll+ELSsY4T5" +
                    "7S8WQOJM8FF/zB05D2Km5bklORSRKkAGy8ax8FUrvdXsqsiz0IQ/GYPNzfifx8VA" +
                    "PWCb8DXdM6Kn9l8IOYfzLQZkh91i/LfBvJ6uBR+SqROZ0/Vw30j+fuDzm3oB1FLi" +
                    "hwIDAQAB" +
                    "-----END PUBLIC KEY-----";
            rsaKeyRepository.saveAndFlush(new RSAKeyEntity("Wv9hbzJhtY7kNeg8uf-kfG9GUTZogcePNleBy2lVcns",true, tool4PublicString,tool4PrivateString));
            rsaKeyRepository.saveAndFlush(new RSAKeyEntity("XKXYskdHB6p1XNsVGQO-iIM9DAb2siIRWvBu-1FJ_sY",false, iss4PublicKey,null));


            PlatformDeployment iss5 = new PlatformDeployment();
            iss5.setClientId("dmccallum-local-platform-2-client-1");
            iss5.setIss("http://localhost:3000");
            iss5.setDeploymentId("idontknowwhattosetthisto");
            String iss5PublicKey = "-----BEGIN PUBLIC KEY-----" +
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2iSH+X2ZfSPhn9C3KwLF" +
                    "Ba5rzFvifgmSWl8f1cdrnVCjeYgiPBVC/5N54GPDHw0t8mC2pqr/g4t+H0oIAGXC" +
                    "Cl9SYrRsDN41g7iPXwWDbC6mFbzKZzsR4vhEGq434bUYS0uTS/HCqyp3J/YjY41X" +
                    "FoYtyr7H1z+bXyAc8Y6X6RZSxJMKk1b5xN5cPnipaXh8pjomDy3fqWA1K+3i9uge" +
                    "qduwxZx8AppinQRqlk/0hXA8DCuEdoESCcf5ZDIt8+m0lLOQJ68jqbd3Y6nO6vDy" +
                    "IZfWeXbG+Lztm6+WfVf1puWddcGUWtwzQyDj/br3bSVZW48bxSgC3HcSWUwb6LeS" +
                    "HwIDAQAB" +
                    "-----END PUBLIC KEY-----";
            iss5.setOidcEndpoint("http://localhost:3000/platforms/2/authorizations/new");
            String tool5PrivateString = "-----BEGIN RSA PRIVATE KEY-----" +
                    "MIIEpAIBAAKCAQEA94DLZjdUAXryMMmUrz8L7CSMnH7nIKLQvWTSwlcSNbXoCXEi" +
                    "z7URUd8u5aNrorE6WwrAeUk5PK5oNZ1iBuZ8qQpMHw/AIH3G4i3m0y8JohcJBPtA" +
                    "jNNPLrvRpMeRMnxdPkA6TcSX1j9IkC4qA5AckzF1qtZl4Y7Jsbxh8e1Vz0fAwUIV" +
                    "bn6HQQqtvLHCOhswBJ28RXm0hll+ELSsY4T57S8WQOJM8FF/zB05D2Km5bklORSR" +
                    "KkAGy8ax8FUrvdXsqsiz0IQ/GYPNzfifx8VAPWCb8DXdM6Kn9l8IOYfzLQZkh91i" +
                    "/LfBvJ6uBR+SqROZ0/Vw30j+fuDzm3oB1FLihwIDAQABAoIBAQDGqT6aITbwqTrV" +
                    "JjiOIdD0DrI+uy3R6F4cZ100WKdpLUW+rmz+w5vPV5FRCcdPo2nvwcWUlM24g1Vq" +
                    "E5WnbEILfRR0qjPQ4KRO0AFC6bKVxF+c+/9oCgS4wlEYLofBOkmtuegSnAY0Fj+s" +
                    "WyGmEnqdO0nzgnvsfKwwWcFUauhBowZXTtqIn1XZZrUWzyq8m5Igb8juTZ4guZtn" +
                    "DTszLiFfap3uj2jz5LHwuk3VdJtzxowI9Vdu0TvMkb8LTTMRf6x4gUTauNFeeMri" +
                    "0BbPLL8Y0yKTjU1OIqHR6uj20fM3l3pwlsOq4wksdj4V8k0eBkvav1d3RF4XTiqj" +
                    "y1z0Q6eBAoGBAP9UCl82gxhhpPZ6vA3E44Wq7a0bGrvrltoF3I5iNCJfInDvdrvn" +
                    "jpPS0aXW/OqBbnO5V6d3ntz7QkWip//SG/+JDtVYiti0l6OTOX4UNwx6OB9Fot9k" +
                    "Z/0TQ7XxpgaV8T0qwTLZQIwd2y4UXTZhHZcCgSgvI9LwUkYqh1iEtow3AoGBAPgn" +
                    "e9+bOzE9M8XekGotC4WyCRdKzwkyowvpKiMGgb0IzaWGxT/sBXLFM9VUJPKuXtKE" +
                    "HHN6kEr55RgWaSUnJKtaxeR71wC0zSEVEKXwWb/LZqU9gXK+5bedh+3EtH/nIk2T" +
                    "VkscU++4MHqHJzgrciNIYI3MwuXtiBuhO9nDoFQxAoGABtJhAJA9Sm55cNhwo9GJ" +
                    "3q1lckWSHkk6G5MihG9dQznVerz7KCQUrCBq14p58W4J3G+sRt4sUW0pJSEmafBc" +
                    "LSSlT8/wzb9tecJaO+MgYWX2j+dVZbCSErHsEjVloTFqY5770Hb7gYLes0l6ABTy" +
                    "LHG1r7Qqud0P5a4HNbc+BVsCgYA6Ij3Rx3LZeSX8z8PzK7RMUMXyb/MKi5NIwgm+" +
                    "BNM17q8GfQkOjYNFPM+ExV6Wf6T1Gj/0CZ1uAxbZSr69FmtvvSco/J4Eq1Z2zpGE" +
                    "3kaYONVGFp+RuwjjuALU/8TtRb9yfvazR+KeAP9SZQEaFot5moYMEhECfl/pYR89" +
                    "R6fysQKBgQCN+FYL8waZBjfYEPVRjSxiSiUW6uyc/GcoR8jWa/t7pThjBCcX9b72" +
                    "DpxsGjKNJWmxv5rwdS2PG/gQ5AEr1O7dVtBPlxOf15aq4ObirRntvpAZn2gtPSmM" +
                    "qTHAXU6gVX3kn3sCWGRwuLWo7H7YJyt7DjSIkmpZIPxHSVyu1iA1Gg==" +
                    "-----END RSA PRIVATE KEY-----";
            String tool5PublicString = "-----BEGIN PUBLIC KEY-----" +
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA94DLZjdUAXryMMmUrz8L" +
                    "7CSMnH7nIKLQvWTSwlcSNbXoCXEiz7URUd8u5aNrorE6WwrAeUk5PK5oNZ1iBuZ8" +
                    "qQpMHw/AIH3G4i3m0y8JohcJBPtAjNNPLrvRpMeRMnxdPkA6TcSX1j9IkC4qA5Ac" +
                    "kzF1qtZl4Y7Jsbxh8e1Vz0fAwUIVbn6HQQqtvLHCOhswBJ28RXm0hll+ELSsY4T5" +
                    "7S8WQOJM8FF/zB05D2Km5bklORSRKkAGy8ax8FUrvdXsqsiz0IQ/GYPNzfifx8VA" +
                    "PWCb8DXdM6Kn9l8IOYfzLQZkh91i/LfBvJ6uBR+SqROZ0/Vw30j+fuDzm3oB1FLi" +
                    "hwIDAQAB" +
                    "-----END PUBLIC KEY-----";
            rsaKeyRepository.saveAndFlush(new RSAKeyEntity("Wv9hbzJhtY7kNeg8uf-kfG9GUTZogcePNleBy2lVcns",true, tool5PublicString,tool5PrivateString));
            rsaKeyRepository.saveAndFlush(new RSAKeyEntity("XKXYskdHB6p1XNsVGQO-iIM9DAb2siIRWvBu-1FJ_sY",false, iss5PublicKey,null));

            // dmccallum blackboard sandbox tool
            PlatformDeployment iss6 = new PlatformDeployment();
            iss6.setClientId("7e21a7d3-6c13-4364-8f77-28aa97087b96");
            iss6.setIss("https://blackboard.com"); // NB expectation is that a tool potentially has just a single trust relationship w all of Blackboard
            iss6.setDeploymentId("idontknowwhattosetthisto"); // This is how you'd distinguish between specific registrations/placements/something
            iss6.setOidcEndpoint("https://vhs.int.bbpd.io/learn/api/public/v1/oauth2/authorizationcode");
            iss6.setJwksEndpoint("https://devportal-playground.saas.bbpd.io/api/v1/management/applications/7e21a7d3-6c13-4364-8f77-28aa97087b96/jwks.json");
            String tool6PrivateString = "-----BEGIN PRIVATE KEY-----" +
                    "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCqznKDVbTkw/58" +
                    "w15OSemHr24t7Biqkv09eW9qd1D0h95pKXk7BfAVgteGYjkVJ82tCJoSNzwq16ox" +
                    "WpRYr9rtRvoatVdlEz5pcXIQ7ksUH287V15w8dRxihhwDxVD0EHGybAZc61thfME" +
                    "FedHIHB588c2MSmLFZ6IOEzJQ/dcSGfTmqRn8s4Qp9Fl76fQSHbJpsmwL+OHPJ8Q" +
                    "mfmyWsH2xIJ0kIN6urPdvSXjhPOhygSl331mRjBx7BPAHTIjSoAMoH7C8KDIxol3" +
                    "I9i/+yN0X2p9Zt2SwZcyTE3qxsbsycTgP/GQagLsA1q2eh43WoMvqLVZ1vfhIUdL" +
                    "ZnJM1tKNAgMBAAECggEAQyXN1tvGtXhnf1y+okBkUjchNWRvff8zFwu6RTyK0tbn" +
                    "FKh9RDrdW1eIqwfKMQrozHFpQGf+e0k1Wxmk4AiiJE2W3REVPmMGUiWLFiTX1x1U" +
                    "eRurwPMQ9WG4obUMQxCIC0IWvKrBLyFNm/mQZmHWVdsEIcXJ8yeYnyyi4yyY/enC" +
                    "9XT1lnhbelmzGekdjKfYLSL3WhxvP3ItXQPHcnZZFXrUmLxNz5ZXVaCVVArNj3Z4" +
                    "n7e2WH3fdxN9qW5XWFAptF3hQAPBP4uhhZkCHrNQQv+HEK3zBaS44Z3F9OXCHM5m" +
                    "bVTOXBcbZjkDEc9jQuoYXPzDME7F8G1jpxxH+7xB8QKBgQDw+6B1KwqgU/03Umn5" +
                    "ZcSjRBzpEoTMR0GAsVyX6ZuOtrDaWM6s82kAW5kkv6Hj11GF7trZ+CnGoACFo+9b" +
                    "ITkS6LHHdbKtoNxn1fA4tfStuFsj1is/eQ+bPsqC6rLyTe87UC9N4PvIEJ1gYLip" +
                    "wa49lrAJF0rnU4FcZRLKGGdqPwKBgQC1c02Ub3GZTQKLQU6FJIyaCkHGQQYiTOJP" +
                    "OiVfBPuoTVpDejkWrcBc8XfgqXhKJ4OpnkDfNISL86yhfyUgv8cVGV1FiBhMZCjr" +
                    "IGbHv2iuWUBj518xUkFmchHVoCKL0j1FwoJu+Veo2+k0S6LM8yK4ZC2m2Ay85arY" +
                    "xVa5+IlYMwKBgQDAoiRuBO4Fo53yryqPdk5txHDnzyngscUmvqCpvs/ekSNmJz9u" +
                    "+VSiOJZ+zcD3hJNiYMEJJFsgr547NgSL8E7TUrdqHieqq8AEMPxKGQlyJMCWMQl5" +
                    "P6i+DL+NT1ekYbAu1bY6pvIdx0pGVIrJCE3kZaRqX/g8WoA5/4G5kzB3uQKBgQCz" +
                    "rPmlgrV6mEdsnb6jGQ4hewamyrzIOlnfGjTnYofQoMhBCUZ+ro9Nec67KRV0WKyH" +
                    "ya1zWQ3nh+PqB308cKsyuWsi7yEghA4AxpUANwnxm2+nffVFYa5hQ5Nldqy+x/Dk" +
                    "ozeFrGKhUyIaJsuPJzO6Y3jRPh9e7g2/manHfdSDAQKBgQCMS2utWOlCUBcJQ9Hc" +
                    "3pLF2BW7lBU2tvhhKSuX4ppzZAxRMdwOppanUsFe99fLPGIOKIC8kUl0KHJ5nzrN" +
                    "QYZCJoQu4666OR9doaKqrkaHDRtE8wAwPGxLJH56JomJOQYx/sAxpPLQS6dnV84G" +
                    "elApP7Nda1IfGqo5oWeCRHSGeQ==" +
                    "-----END PRIVATE KEY-----";
            String tool6PublicString = "-----BEGIN PUBLIC KEY-----" +
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqs5yg1W05MP+fMNeTknp" +
                    "h69uLewYqpL9PXlvandQ9IfeaSl5OwXwFYLXhmI5FSfNrQiaEjc8KteqMVqUWK/a" +
                    "7Ub6GrVXZRM+aXFyEO5LFB9vO1decPHUcYoYcA8VQ9BBxsmwGXOtbYXzBBXnRyBw" +
                    "efPHNjEpixWeiDhMyUP3XEhn05qkZ/LOEKfRZe+n0Eh2yabJsC/jhzyfEJn5slrB" +
                    "9sSCdJCDerqz3b0l44TzocoEpd99ZkYwcewTwB0yI0qADKB+wvCgyMaJdyPYv/sj" +
                    "dF9qfWbdksGXMkxN6sbG7MnE4D/xkGoC7ANatnoeN1qDL6i1Wdb34SFHS2ZyTNbS" +
                    "jQIDAQAB" +
                    "-----END PUBLIC KEY-----";
            // Leave these alone for now... now sure how the tool's key pair is used ATM, and we want to get the
            // platform PK from its JWK URL
//            rsaKeyRepository.saveAndFlush(new RSAKeyEntity("Wv9hbzJhtY7kNeg8uf-kfG9GUTZogcePNleBy2lVcns",true, tool6PublicString,tool6PrivateString));
//            rsaKeyRepository.saveAndFlush(new RSAKeyEntity("XKXYskdHB6p1XNsVGQO-iIM9DAb2siIRWvBu-1FJ_sY",false, iss6PublicKey,null));

            // dmccallum local Canvas tool
            PlatformDeployment iss7 = new PlatformDeployment();
            iss7.setClientId("4"); // This has to be the DeveloperKey ID in Canvas
            iss7.setIss("https://canvas.instructure.com");
            iss7.setDeploymentId("idontknowwhattosetthisto"); // This is how you'd distinguish between specific registrations/placements/something
            iss7.setOidcEndpoint("http://localhost:3000/api/lti/authorize"); // /authorize will display request info, /authorize_redirect will actually send auth back to tool
            iss7.setJwksEndpoint("http://localhost:3000/api/lti/security/jwks");
            String tool7PrivateString = "-----BEGIN RSA PRIVATE KEY-----" +
                    "MIIEpQIBAAKCAQEA7i2h/CGg3tCqVqjhiMMcym6LUYARB/wu0UAGSSaKy//QWPiV" +
                    "VZrHI8HsNmyM+Gk/0mLS+OtubkEyWmsNW43/cCoPZoZvuxiMITM48Bly7GtO4tOD" +
                    "FLDwY6VIXIc2RQTC7NoxkKqyXAz1n8JNxIwoY2EQvVKEqV2mbCF1ET7RmTmkcQIy" +
                    "Wy7pWLXsGNRt972CEhg+FXRTwo0l4EzOzLMmgqsqX/gx3ZnHNHj/zrHVu6R91jIY" +
                    "m0XzXUtXVNZkoFGO8HRF9DL0VOVvJ7LJ/zA0B/HJV4GIguZjy6WH7+kBJC7qUPbH" +
                    "x23Bp6HMSV5WEmtVTHv4dqCfT5W3anJ2bx65PwIDAQABAoIBAQC/X2cSISsTHPJg" +
                    "4x0kODF/7cZ/W7lTgytS8nCagh9vvkH32vSSbeSoNH7JU/sI9fhT4RfG7dQzPqto" +
                    "vy9iavvdcc4iAM7xs82UN4X+VVRj5ezikDR4gZsFVZ6IyhOu7R56FO9SX/UjxT3u" +
                    "eb7mt/8TqsA6fn0dYKQBqfhYPPNIet1MOEmVPHNGBm+qqDiMi/VymClS4Gu7vCzE" +
                    "AYptdcWtfPqt327pgs4cBQJLwk+Q1uDL+EBZiZNoKpfdfGn0xyO5kHxrxdHdsDJV" +
                    "YNjhRIZNxpyM+Mp2OEmgn2EGJhhXU9fV+4Jn2GoEPqwk78ZCjG+Nydivep2HQ4id" +
                    "/BerHWfRAoGBAP5HuplBmEdRYLE56JE5G/BfOJhT80q0islkbfPFsGJhXfAGh7Jv" +
                    "IB51wKuIlipAu/n7PdeqFgt95dt1BVfMPRNRKhPpXO1crqxBPzBjVz2e67xv4s4G" +
                    "5flvu4aLiQULEQvYfsrPXIaG2pcLquQVU2Hn2yUmH+aaSPQLySF6siUVAoGBAO/K" +
                    "BjiDR08qeUPta0ckGWCfmrOEzclljn+GbagrrPu2BfdkTzE5UgAabE12xPPfc+/H" +
                    "HSXG2mBpUaEOG2VSjZJ6VMszG6f/HfUhxD4JL9c3jsP+PTzMZgDb2JLXr4l0o7Y6" +
                    "OXd0S61kFAdIH+GjPSQkygUem2+NUqzm6Hc7jaIDAoGAYWvv5QnJowFdr2kFXqZn" +
                    "uudwkoQPgX68cb6FldEW7Apr6CtIUCfdQW5R/CZfih61JmiI4BjtS0fYCgXCn0nD" +
                    "2KPPIEPXc6V/0sVIxWw6H4TEwrSHS/I+qBuT2E+blpAP3t76bQ+XXn9KPnac6bf1" +
                    "81vWjVwW+s3/x2jAjZApsqUCgYEA23MeolWyy0bp2DrOdTsaZo1q/WRLGUfyU89p" +
                    "SAFAvNRnPXP17eyxmRqVh4ds1GvqJMUrbclBTf7VD/pjJCWbHzUJqTXK18LpXdBb" +
                    "UxMBT4uvArwIuz2tFJge6rSuF7RBp6I9uY8bO0Jd+hQN6WiFqjBpGqpaQosp4Mcq" +
                    "dQRxqJUCgYEAxrjshdUcf+9x4cIpq8iBTw9e+40UG8206TESPQZ+aqVaeQCz/qAB" +
                    "HbEbmkzzqyxzmLow2uH/DRMc3Pva9ryx6g0qlgFgEkySVp3+d/zRo11IkOtjfc0N" +
                    "KDC+q8752493+CF7/an5Uj3+V7RL3VjYekuDMM7VjnAgyu3ENoWXDfg=" +
                    "-----END RSA PRIVATE KEY-----";
            String tool7PublicString = "-----BEGIN PUBLIC KEY-----" +
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7i2h/CGg3tCqVqjhiMMc" +
                    "ym6LUYARB/wu0UAGSSaKy//QWPiVVZrHI8HsNmyM+Gk/0mLS+OtubkEyWmsNW43/" +
                    "cCoPZoZvuxiMITM48Bly7GtO4tODFLDwY6VIXIc2RQTC7NoxkKqyXAz1n8JNxIwo" +
                    "Y2EQvVKEqV2mbCF1ET7RmTmkcQIyWy7pWLXsGNRt972CEhg+FXRTwo0l4EzOzLMm" +
                    "gqsqX/gx3ZnHNHj/zrHVu6R91jIYm0XzXUtXVNZkoFGO8HRF9DL0VOVvJ7LJ/zA0" +
                    "B/HJV4GIguZjy6WH7+kBJC7qUPbHx23Bp6HMSV5WEmtVTHv4dqCfT5W3anJ2bx65" +
                    "PwIDAQAB" +
                    "-----END PUBLIC KEY-----";
            // Leave these alone for now... now sure how the tool's key pair is used ATM, and we want to get the
            // platform PK from its JWK URL
//            rsaKeyRepository.saveAndFlush(new RSAKeyEntity("Wv9hbzJhtY7kNeg8uf-kfG9GUTZogcePNleBy2lVcns",true, tool6PublicString,tool6PrivateString));
//            rsaKeyRepository.saveAndFlush(new RSAKeyEntity("XKXYskdHB6p1XNsVGQO-iIM9DAb2siIRWvBu-1FJ_sY",false, iss6PublicKey,null));


            // create our sample user
            LtiUserEntity user = ltiUserRepository.saveAndFlush(new LtiUserEntity("azeckoski", null));
            ltiUserRepository.saveAndFlush(user);
        }
    }

    public String getOwnPrivateKey() {
        return ownPrivateKey;
    }

    public void setOwnPrivateKey(String ownPrivateKey) {
        this.ownPrivateKey = ownPrivateKey;
    }

    public String getOwnPublicKey() {
        return ownPublicKey;
    }

    public void setOwnPublicKey(String ownPublicKey) {
        this.ownPublicKey = ownPublicKey;
    }
}
