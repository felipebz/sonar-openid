/*
 * Sonar OpenID Plugin
 * Copyright (C) 2012-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.openid;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.ParameterList;
import org.sonar.api.config.Settings;
import org.sonar.api.security.UserDetails;
import org.sonar.api.server.authentication.Display;
import org.sonar.api.server.authentication.OAuth2IdentityProvider;
import org.sonar.api.server.authentication.UserIdentity;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public class OpenIdIdentityProvider implements OAuth2IdentityProvider {

  public static final String KEY = "openid";
  private static final Logger LOG = Loggers.get(OpenIdIdentityProvider.class);

  private Settings settings;
  private OpenIdClient openIdClient;

  public OpenIdIdentityProvider(Settings settings, OpenIdClient openIdClient) {
    this.settings = settings;
    this.openIdClient = openIdClient;
  }

  @Override
  public boolean allowsUsersToSignUp() {
    return settings.getBoolean("sonar.auth.openid.allowUsersToSignUp");
  }

  @Override
  public Display getDisplay() {
    return Display.builder()
        .setIconPath("/static/openid/openid.png")
        .setBackgroundColor("#555555")
        .build();
  }

  @Override
  public String getKey() {
    return KEY;
  }

  @Override
  public String getName() {
    return "OpenID";
  }

  @Override
  public boolean isEnabled() {
    return settings.getBoolean("sonar.auth.openid.enabled");
  }

  @Override
  public void init(InitContext context) {
    AuthRequest authRequest = openIdClient.createAuthenticationRequest();
    context.redirectTo(authRequest.getDestinationUrl(true));
  }

  @Override
  public void callback(CallbackContext context) {
    try {
      onCallback(context);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public void onCallback(CallbackContext context) throws IOException {

    ParameterList responseParameters = new ParameterList(context.getRequest().getParameterMap());
    String receivingURL = requestUrl(context.getRequest());

    UserDetails user;
    try {
      user = openIdClient.verify(receivingURL, responseParameters);
    } catch (RuntimeException e) {
      LOG.error("Fail to verify OpenId request", e);
      throw e;
    }

    UserIdentity userIdentity = UserIdentity.builder()
        .setProviderLogin(user.getName()) // TODO review
        .setLogin(user.getName()) // TODO review
        .setName(user.getName())
        .setEmail(user.getEmail())
        .build();
    context.authenticate(userIdentity);
    context.redirectToRequestedPage();
  }

  String requestUrl(HttpServletRequest httpRequest) {
    StringBuilder receivingURL = new StringBuilder(openIdClient.getReturnToUrl());
    String queryString = httpRequest.getQueryString();
    if (StringUtils.isNotEmpty(queryString)) {
      // the return-to url does not contain ? (see
      // OpenIdClient#initReturnToUrl()
      receivingURL.append("?").append(queryString);
    }
    return receivingURL.toString();
  }

}
