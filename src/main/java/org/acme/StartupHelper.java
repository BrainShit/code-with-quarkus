package org.acme;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;
import javax.transaction.Transactional;

@Singleton
public class StartupHelper {

    @Transactional
    public void startup(@Observes StartupEvent evt){
        User user = new User();
        user.username = "admin";
        user.password = BcryptUtil.bcryptHash("admin");
        user.role = "ADMIN";
        user.persist();
    }
}
