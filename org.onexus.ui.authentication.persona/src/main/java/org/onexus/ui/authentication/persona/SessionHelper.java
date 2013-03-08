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
package org.onexus.ui.authentication.persona;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;
import org.apache.wicket.util.lang.Args;

/**
 * Utility class that stores the {@link BrowserId} (authentication data) in the current web session.
 */
public class SessionHelper
{

    private static final MetaDataKey<BrowserId> KEY = new MetaDataKey<BrowserId>()
    {

        private static final long serialVersionUID = 1L;
    };

    /**
     * @param session
     *            the current web session
     * @return the authentication data. May be {@code null}.
     */
    public static BrowserId getBrowserId(final Session session)
    {

        Args.notNull(session, "session");

        BrowserId browserId = session.getMetaData(KEY);

        return browserId;
    }

    /**
     *
     * @param session
     *            the current web session
     * @return {@code true} if there is authentication data. {@code false} - otherwise.
     */
    public static boolean isLoggedIn(final Session session)
    {
        return getBrowserId(session) != null;
    }

    /**
     * Stores the authentication data in the current web session
     *
     * @param session
     *            the current web session
     * @param browserId
     *            the authentication data
     */
    public static void logIn(final Session session, final BrowserId browserId)
    {
        Args.notNull(session, "session");
        Args.notNull(browserId, "browserId");

        session.setMetaData(KEY, browserId);
        ;
    }

    /**
     * Removes the authentication data from the current web session
     *
     * @param session
     *            the current web session
     */
    public static void logOut(final Session session)
    {
        Args.notNull(session, "session");

        session.setMetaData(KEY, null);
        ;
    }
}
