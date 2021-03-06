package com.mesosphere.sdk.kafka.scheduler;

import com.mesosphere.sdk.config.validate.ConfigValidator;
import com.mesosphere.sdk.specification.ServiceSpec;
import com.mesosphere.sdk.testing.ConfigValidatorUtils;
import com.mesosphere.sdk.testing.ServiceTestRunner;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ServiceTest {
    private static final ConfigValidator<ServiceSpec> validator = new KafkaZoneValidator();

    @Test
    public void testSpec() throws Exception {
        getDefaultRunner().run();
    }

    @Test
    public void rejectRackEnablement() throws Exception {
        ConfigValidatorUtils.rejectRackEnablement(validator, getDefaultRunner(), "PLACEMENT_CONSTRAINTS");
    }

    @Test
    public void rejectRackDisablement() throws Exception {
        ConfigValidatorUtils.rejectRackDisablement(validator, getDefaultRunner(), "PLACEMENT_CONSTRAINTS");
    }

    @Test
    public void allowRackChanges() throws Exception {
        ConfigValidatorUtils.allowRackChanges(validator, getDefaultRunner(), "PLACEMENT_CONSTRAINTS");
    }

    private ServiceTestRunner getDefaultRunner() {
        Map<String, String> map = new HashMap<>();
        map.put("KAFKA_ZOOKEEPER_URI", "/path/to/zk"); // set by our Main.java
        map.put("SETUP_HELPER_ADVERTISED_LISTENERS", "advertised.listeners=FAKE"); // set by setup-helper
        map.put("SETUP_HELPER_LISTENERS", "listeners=FAKE"); // set by setup-helper
        map.put("SETUP_HELPER_SECURITY_INTER_BROKER_PROTOCOL", "fake"); // set by setup-helper
        map.put("SETUP_HELPER_SUPER_USERS", "User:fake"); // set by setup-helper

        return new ServiceTestRunner()
                .setPodEnv("kafka", map)
                .setBuildTemplateParams("kafka-version", "2.11-1.0.0"); // set by build.sh/versions.sh
    }
}
