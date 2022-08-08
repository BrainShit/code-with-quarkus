package org.acme;

import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.FormAuthenticationMechanism;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.quarkus.vertx.http.runtime.security.HttpCredentialTransport;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Set;

@Alternative
@Priority(1)
@ApplicationScoped
public class CustomFormAuthenticationMechanism implements HttpAuthenticationMechanism {
    @Inject
    FormAuthenticationMechanism delegate;

    @Override
    public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager) {
        return delegate.authenticate(context, identityProviderManager);
    }

    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext context) {
        Uni<ChallengeData> rValue = delegate.getChallenge(context);
        return rValue.onItem().transform(challengeData -> {
            if (challengeData.status == Response.Status.FOUND.getStatusCode()) {
                return new ChallengeData(Response.Status.UNAUTHORIZED.getStatusCode(), challengeData.headerName, challengeData.headerContent);
            }
            return challengeData;
        });
    }

    @Override
    public Set<Class<? extends AuthenticationRequest>> getCredentialTypes() {
        return delegate.getCredentialTypes();
    }

    @Override
    public HttpCredentialTransport getCredentialTransport() {
        return delegate.getCredentialTransport();
    }
//
//    @Override
//    public Uni<HttpCredentialTransport> getCredentialTransport(RoutingContext context) {
//        return delegate.getCredentialTransport(context);
//    }

    @Override
    public Uni<Boolean> sendChallenge(RoutingContext context) {
        return getChallenge(context).map(new ChallengeSender(context));
    }

}
