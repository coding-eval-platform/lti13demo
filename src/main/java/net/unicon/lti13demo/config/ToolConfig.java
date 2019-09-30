package net.unicon.lti13demo.config;

import lombok.AllArgsConstructor;
import net.unicon.lti13demo.model.PlatformDeployment;
import net.unicon.lti13demo.model.RSAKeyEntity;
import net.unicon.lti13demo.repository.PlatformDeploymentRepository;
import net.unicon.lti13demo.repository.RSAKeyRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Juan Marcos Bellini on 2019-09-29.
 */
@Configuration
@AllArgsConstructor
public class ToolConfig implements InitializingBean {

    private final PlatformDeploymentRepository platformDeploymentRepository;

    private final RSAKeyRepository rsaKeyRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        storeBlackboard();
        storeKey();
    }

    private void storeBlackboard() {
        final PlatformDeployment platformDeployment = new PlatformDeployment();
        platformDeployment.setIss("https://blackboard.com");
        platformDeployment.setClientId("cff37769-da98-48a8-961b-bd3770685fa6");
        platformDeployment.setOidcEndpoint("https://developer.blackboard.com/api/v1/gateway/oauth2/jwttoken");
        platformDeployment.setDeploymentId("ed1b1363-1e56-4014-a196-a76bebd67747");

        platformDeploymentRepository.save(platformDeployment);
    }

    private void storeKey() {
        final RSAKeyEntity key = new RSAKeyEntity(
                "OWNKEY",
                true,
//                "tj2ec27mwhSQyS4WPbmByFW2Q9wQRLMWOPev7imUyyfeO_nXCiWYUjQBD0h9JxcYysqfe44QXvU11zyXlenquiDxLc-bEnmKbS59LyfLSlZlCHz1sdEZMlwefJGaPtTcnSwhcphR8JpTbxqH-8CV9-xUbHHparmHgK9Vw2XJJoShJjIW2Jq2Y4gELm0nfrKhoT97SvQ59K3PyWI4rk0RLAz2C2w_2TACJ1pDbjLn5SI1veYlIHXSdu3TsQ7A0sv0KzraWLVfY7nzk7u7v_BKl0zZHfm7KkukTZU8_Q0G2V5Dqqats34X2iLjnGXq6vd_Aq0wxLxI9CjUGhNqFC2RxV0PfrczzFtCm9gFNGd2UxlV2H4o_4CjTdBWO7zNAL_AGvGGrgkD8nm_KzH13gTlAt7DW3WMEJD7TBB4OOeCE2z4eIpFhoF9hYuLS1yjPm7Gk8U-qEuxCz_yoCCkk8IIKzZptvLtLrI7Y_63KuWMNtCp4Q-gMUuJzQYsp9uMAEp-ZLTD2ZEyRooNT1tHpoGoLrcxmRLDUphzQdrdy9MG7VEdkZsMp40Y8L1QjoQEUm-9SKz2BOWtOaBMHnvnvuAsawIR50YOTYQrmHj2ATIWvl3Z3bytZgVXsmcxmsMm35u2TgwTLUePGCByMac0aB1Wmb6uOZyXNNLZbhUmxmxyBNc",
                "",
                "-----BEGIN PRIVATE KEY-----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-----END PRIVATE KEY-----"
        );
        rsaKeyRepository.save(key);
    }
}
