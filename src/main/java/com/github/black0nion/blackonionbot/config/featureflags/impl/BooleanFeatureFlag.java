package com.github.black0nion.blackonionbot.config.featureflags.impl;

import javax.annotation.Nonnull;

/**
 * The Value will never be null, it's either true or false.
 */
public class BooleanFeatureFlag extends AbstractFeatureFlag<Boolean> {

	public BooleanFeatureFlag(Boolean value) {
		super(value != null && value);
	}

	@Override
	@Nonnull
	public Boolean getValue() {
		return super.getValue();
	}

	/**
	 * @return true if the feature flag is enabled, false otherwise
	 */
	@Override
	public boolean isEnabled() {
		return getValue();
	}

	@Override
	public boolean isDisabled() {
		return !getValue();
	}
}
