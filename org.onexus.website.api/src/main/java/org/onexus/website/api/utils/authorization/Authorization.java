/**
 *  Copyright 2012 Universitat Pompeu Fabra.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package org.onexus.website.api.utils.authorization;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.onexus.resource.api.session.LoginContext;
import org.onexus.website.api.utils.parser.BooleanExpressionEvaluator;

import java.util.Set;

public class Authorization {

    public static String ANONYMOUS = "anonymous";

    public static boolean authorize(IAuthorization target) {
        if (target != null && target.getAuthorization() != null) {

            final Set<String> roles = LoginContext.get().getRoles();
            final String userName = LoginContext.get().getUserName();
            BooleanExpressionEvaluator evaluator = new BooleanExpressionEvaluator(target.getAuthorization()) {
                @Override
                protected boolean evaluateToken(String token) {

                    if (ANONYMOUS.equalsIgnoreCase(token)) {
                        return !AuthenticatedWebSession.get().isSignedIn();
                    }

                    return token.equals(userName) || roles.contains(token);
                }
            };
            return evaluator.evaluate();
        }
        return true;
    }
}
