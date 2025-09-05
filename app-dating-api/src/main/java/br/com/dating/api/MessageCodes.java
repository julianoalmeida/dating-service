package br.com.dating.api;

public final class MessageCodes {

	  public static final String REQUIRED_FIELD = "required.field";

	  public static final String INVALID_PATTERN = "invalid.pattern";

	  public static final String INVALID_VALUE = "invalid.value";

	  public static final String NONUNIQUE_VALUE = "nonunique.value";

	  private MessageCodes() {
	    throw new IllegalStateException();
	  }
}
