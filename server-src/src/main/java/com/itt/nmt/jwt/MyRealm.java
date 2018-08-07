package com.itt.nmt.jwt;

import com.itt.ApplicationContextProvider;
import com.itt.nmt.models.User;
import com.itt.nmt.services.UserService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

/**
 * The Class MyRealm.
 */
@Controller
public class MyRealm extends AuthorizingRealm {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LogManager.getLogger(MyRealm.class);

    /**
     * Instantiates a new my realm.
     */

    /*
     * (non-Javadoc)
     * @see
     * org.apache.shiro.realm.AuthenticatingRealm#supports(org.apache.shiro.
     * authc.AuthenticationToken)
     */
    @Override
    public boolean supports(final AuthenticationToken token) {

        return token instanceof JWTToken;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.apache.shiro.realm.AuthorizingRealm#doGetAuthorizationInfo(org.apache
     * .shiro.subject.PrincipalCollection)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(final PrincipalCollection principals) {
        // Add Authorization code here.
        return new SimpleAuthorizationInfo();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.apache.shiro.realm.AuthenticatingRealm#doGetAuthenticationInfo(org.
     * apache.shiro.authc.AuthenticationToken)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken auth)
            throws AuthenticationException {

        ApplicationContext ctx = ApplicationContextProvider.getContext();
        UserService userService = ctx.getBean(UserService.class);
        String token = (String) auth.getCredentials();

        String email = JWTUtil.getemail(token);
        if (email == null) {
            throw new AuthenticationException("token invalid");
        }

        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new AuthenticationException("User didn't existed!");
        }

        if (!JWTUtil.verify(token, email, user.getId()) || !user.isSession()) {
            throw new AuthenticationException("Invalid Token");
        }


        AuthenticationInfo authenticationInfo = null;

        authenticationInfo = new SimpleAuthenticationInfo(token, token, "my_realm");

        return authenticationInfo;
    }
}