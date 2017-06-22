/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2017 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.restlesscoder.life;

import java.math.BigDecimal;

import net.imagej.types.BigComplex;
import net.imagej.types.DataType;

import org.scijava.AbstractContextual;
import org.scijava.plugin.Plugin;

/**
 * {@link DataType} definition for booleans.
 * 
 * @author Curtis Rueden
 */
@Plugin(type = DataType.class)
public class DataTypeBoolean extends AbstractContextual implements
	DataType<NativeBoolType>
{

	private final NativeBoolType type = new NativeBoolType();

	@Override
	public NativeBoolType getType() {
		return type;
	}

	@Override
	public String shortName() {
		return "boolean";
	}

	@Override
	public String longName() {
		return "boolean";
	}

	@Override
	public String description() {
		return "A boolean type: either true or false";
	}

	@Override
	public boolean isComplex() {
		return false;
	}

	@Override
	public boolean isFloat() {
		return false;
	}

	@Override
	public boolean isSigned() {
		return false;
	}

	@Override
	public boolean isBounded() {
		return true;
	}

	@Override
	public void lowerBound(NativeBoolType dest) {
		dest.set(false);
	}

	@Override
	public void upperBound(NativeBoolType dest) {
		dest.set(true);
	}

	@Override
	public int bitCount() {
		return 8;
	}

	@Override
	public NativeBoolType createVariable() {
		return new NativeBoolType();
	}

	@Override
	public void cast(NativeBoolType val, BigComplex dest) {
		dest.setReal(val.get() ? 1 : 0);
		dest.setImag(BigDecimal.ZERO);
	}

	@Override
	public void cast(BigComplex val, NativeBoolType dest) {
		setLong(dest, val.getReal().longValue());
	}

	@Override
	public boolean hasDoubleRepresentation() {
		return true;
	}

	@Override
	public boolean hasLongRepresentation() {
		return true;
	}

	@Override
	public double asDouble(NativeBoolType val) {
		return val.get() ? 1 : 0;
	}

	@Override
	public long asLong(NativeBoolType val) {
		return val.get() ? 1 : 0;
	}

	@Override
	public void setDouble(NativeBoolType val, double v) {
		val.set(v != 0);
	}

	@Override
	public void setLong(NativeBoolType val, long v) {
		val.set(v != 0);
	}

}
