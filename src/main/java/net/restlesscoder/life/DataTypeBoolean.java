
package net.restlesscoder.life;

import java.math.BigDecimal;

import net.imagej.types.BigComplex;
import net.imagej.types.DataType;
import net.imglib2.type.logic.NativeBoolType;

import org.scijava.AbstractContextual;
import org.scijava.plugin.Plugin;

/**
 * {@link DataType} definition for boolean.
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
		return "A boolean (true or false) data type";
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
	public void lowerBound(final NativeBoolType dest) {
		dest.set(false);
	}

	@Override
	public void upperBound(final NativeBoolType dest) {
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
	public void cast(final NativeBoolType val, final BigComplex dest) {
		dest.setReal(asDouble(val));
		dest.setImag(BigDecimal.ZERO);
	}

	@Override
	public void cast(final BigComplex val, final NativeBoolType dest) {
		dest.set(!val.getReal().equals(BigDecimal.ZERO));
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
	public double asDouble(final NativeBoolType val) {
		return val.getRealDouble();
	}

	@Override
	public long asLong(final NativeBoolType val) {
		return val.getIntegerLong();
	}

	@Override
	public void setDouble(final NativeBoolType val, final double v) {
		val.setReal(v);
	}

	@Override
	public void setLong(final NativeBoolType val, final long v) {
		val.setInteger(v);
	}
}
