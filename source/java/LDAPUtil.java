/*---------------------------------------------------------------
*  Copyright 2011 by the Radiological Society of North America
*
*  This source software is released under the terms of the
*  RSNA Public License (http://mirc.rsna.org/rsnapubliclicense)
*----------------------------------------------------------------*/

package org.rsna.ldap;

import javax.naming.*;
import javax.naming.directory.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;

/**
 * Demonstrates how to create an initial context to an LDAP server
 * using simple authentication.
 *
 * usage: java Simple
 */
public class LDAPUtil {

	/**
	 * Connect to an LDAP provider
	 * @param initialContextFactory the factory class (e.g., "com.sun.jndi.ldap.LdapCtxFactory")
	 * @param providerURL the URL of the provider (e.g., "ldap://ip:port/path")
	 * @param securityAuthentication the authentication type (e.g., "simple")
	 * @param securityPrincipal the username (e.g., "cn=S. User, ou=NewHires, o=JNDITutorial"
	 * @param securityCredentials the password (e.g., "mysecret")
	 * @param baseDN the base directory name
	 * @param searchFilter the filter field (e.g., "(&(objectClass=user)(sAMAccountName=username))")
	 * @param returnedAttributes the list of attributes to be found, separated by | characters (e.g., "memberof")
	 */
    public static String connect(
			String initialContextFactory,
			String providerURL,
			String securityAuthentication,
			String securityPrincipal,
			String securityCredentials,
			String baseDN,
			String searchFilter,
			String returnedAttributes) {

		Hashtable<String,String> env = new Hashtable<String,String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
		env.put(Context.PROVIDER_URL, providerURL);
		env.put(Context.SECURITY_AUTHENTICATION, securityAuthentication);
		env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
		env.put(Context.SECURITY_CREDENTIALS, securityCredentials);

		String[] ras = returnedAttributes.split("|");
		SearchControls searchCtls = new SearchControls();
		searchCtls.setReturningAttributes(ras);
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		StringWriter sw = new StringWriter();
		try {
			DirContext ctx = new InitialDirContext(env);
			sw.append("DirContext class: "+ctx.getClass().getName()+"\n");

			NamingEnumeration<SearchResult> ldapResponse = ctx.search(baseDN, searchFilter, searchCtls);

			int item = 0;
			while (ldapResponse.hasMore()) {
				sw.append("\nSearch Result " + item + "\n");
				SearchResult sr = ldapResponse.next();
				Attributes attrs = sr.getAttributes();
				NamingEnumeration<String> ids = attrs.getIDs();

				while (ids.hasMore()) {
					String id = ids.next();
					Attribute attr = attrs.get(id);
					Object value = attr.get();
					sw.append("    " + id + " = " + value.toString()+ "\n");
				}

				sw.append("\n");
				item++;
			}

			ctx.close();
		}
		catch (Exception ex) {
			sw.append("\n\nException:\n");
			ex.printStackTrace(new PrintWriter(sw));
		}
		return sw.toString();
    }
}
