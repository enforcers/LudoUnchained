/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2013-05-23 17:46:09 UTC)
 * on 2013-05-26 at 11:28:21 UTC 
 * Modify at your own risk.
 */

package com.appspot.ludounchained.usercontrollerEndpoint.model;

/**
 * Model definition for Session.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the . For a detailed explanation see:
 * <a href="http://code.google.com/p/google-api-java-client/wiki/Json">http://code.google.com/p/google-api-java-client/wiki/Json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Session extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private com.google.api.client.util.DateTime createdAt;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String sessionId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private User user;

  /**
   * @return value or {@code null} for none
   */
  public com.google.api.client.util.DateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * @param createdAt createdAt or {@code null} for none
   */
  public Session setCreatedAt(com.google.api.client.util.DateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getSessionId() {
    return sessionId;
  }

  /**
   * @param sessionId sessionId or {@code null} for none
   */
  public Session setSessionId(java.lang.String sessionId) {
    this.sessionId = sessionId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public User getUser() {
    return user;
  }

  /**
   * @param user user or {@code null} for none
   */
  public Session setUser(User user) {
    this.user = user;
    return this;
  }

  @Override
  public Session set(String fieldName, Object value) {
    return (Session) super.set(fieldName, value);
  }

  @Override
  public Session clone() {
    return (Session) super.clone();
  }

}
