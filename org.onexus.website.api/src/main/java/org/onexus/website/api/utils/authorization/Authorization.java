package org.onexus.website.api.utils.authorization;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.onexus.website.api.utils.parser.BooleanExpressionEvaluator;

public class Authorization {

    public static String ANONYMOUS = "anonymous";

    public static boolean authorize(IAuthorization target) {
        if (target != null && target.getAuthorization() != null) {

            final Roles roles = AuthenticatedWebSession.get().getRoles();
            BooleanExpressionEvaluator evaluator = new BooleanExpressionEvaluator(target.getAuthorization()) {
                @Override
                protected boolean evaluateToken(String token) {

                    if (ANONYMOUS.equalsIgnoreCase(token)) {
                        return !AuthenticatedWebSession.get().isSignedIn();
                    }

                    return roles.hasRole(token);
                }
            };
            return evaluator.evaluate();
        }
        return true;
    }
}
