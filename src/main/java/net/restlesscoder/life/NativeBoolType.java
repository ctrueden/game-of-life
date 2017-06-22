/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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

import java.math.BigInteger;

import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.type.BooleanType;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.integer.AbstractIntegerType;
import net.imglib2.util.Fraction;

/**
 * TODO
 *
 * @author Curtis Rueden
 */
public class NativeBoolType extends AbstractIntegerType< NativeBoolType > implements BooleanType< NativeBoolType >, NativeType< NativeBoolType >
{
	// Maximum count is Integer.MAX_VALUE * (8 / getBitsPerPixel())
	protected int i = 0;

	final protected NativeImg< ?, ? extends BoolAccess > img;

	// the DataAccess that holds the information
	protected BoolAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public NativeBoolType( final NativeImg< ?, ? extends BoolAccess > bitStorage )
	{
		img = bitStorage;
	}

	// this is the constructor if you want it to be a variable
	public NativeBoolType( final boolean value )
	{
		this( ( NativeImg< ?, ? extends BoolAccess > ) null );
		dataAccess = new BoolArray( 1 );
		set( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public NativeBoolType( final BoolAccess access )
	{
		this( (NativeImg<NativeBoolType, ? extends BoolAccess>)null );
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public NativeBoolType()
	{
		this( false );
	}

	@Override
	public NativeImg<NativeBoolType, ? extends BoolAccess> createSuitableNativeImg( final NativeImgFactory<NativeBoolType> storageFactory, final long dim[] )
	{
		// create the container
		// HACK: storageFactory cannot make a boolean[], so we ignore it :-(
		final NativeImg<NativeBoolType, ? extends BoolAccess> container = new ArrayImg< >( new BoolArray( 1 ), dim, new Fraction() );

		// create a Type that is linked to the container
		final NativeBoolType linkedType = new NativeBoolType( container );

		// pass it to the NativeContainer
		container.setLinkedType( linkedType );

		return container;
	}

	@Override
	public void updateContainer( final Object c )
	{
		dataAccess = img.update( c );
	}

	@Override
	public NativeBoolType duplicateTypeOnSameNativeImg()
	{
		return new NativeBoolType( img );
	}

	@Override
	public boolean get()
	{
		return dataAccess.getValue( i );
	}

	@Override
	public void set( final boolean value )
	{
		dataAccess.setValue( i, value );
	}

	@Override
	public int getInteger()
	{
		return get() ? 1 : 0;
	}

	@Override
	public long getIntegerLong()
	{
		return get() ? 1 : 0;
	}

	@Override
	public BigInteger getBigInteger()
	{
		return get() ? BigInteger.ONE : BigInteger.ZERO;
	}

	@Override
	public void setInteger( final int f )
	{
		if ( f >= 1 )
			set( true );
		else
			set( false );
	}

	@Override
	public void setInteger( final long f )
	{
		if ( f >= 1 )
			set( true );
		else
			set( false );
	}

	@Override
	public void setBigInteger(final BigInteger b)
	{
		if ( b.compareTo(BigInteger.ZERO) > 0 )
			set ( true );
		else
			set( false );
	}

	@Override
	public double getMaxValue()
	{
		return 1;
	}

	@Override
	public double getMinValue()
	{
		return 0;
	}

	@Override
	public void set( final NativeBoolType c ) { set( c.get() ); }

	@Override
	public void and( final NativeBoolType c ) { set( get() && c.get() ); }

	@Override
	public void or( final NativeBoolType c ) { set( get() || c.get() ); }

	@Override
	public void xor( final NativeBoolType c ) { set( get() ^ c.get() ); }

	@Override
	public void not() { set( !get() ); }

	@Override
	public void add( final NativeBoolType c )
	{
		xor( c );
	}

	@Override
	public void div( final NativeBoolType c )
	{
		and( c );
	}

	@Override
	public void mul( final NativeBoolType c )
	{
		and( c );
	}

	@Override
	public void sub( final NativeBoolType c )
	{
		xor( c );
	}

	@Override
	public void mul( final float c )
	{
		if ( c >= 0.5f )
			set( get() && true );
		else
			set( false );
	}

	@Override
	public void mul( final double c )
	{
		if ( c >= 0.5f )
			set( get() && true );
		else
			set( false );
	}

	@Override
	public void setOne() { set( true ); }

	@Override
	public void setZero() { set( false ); }

	@Override
	public void inc() { not(); }

	@Override
	public void dec() { not(); }

	@Override
	public int compareTo( final NativeBoolType c )
	{
		final boolean b1 = get();
		final boolean b2 = c.get();

		if ( b1 && !b2 )
			return 1;
		else if ( !b1 && b2 )
			return -1;
		else
			return 0;
	}

	@Override
	public NativeBoolType createVariable()
	{
		return new NativeBoolType();
	}

	@Override
	public NativeBoolType copy(){ return new NativeBoolType( get() ); }

	@Override
	public String toString()
	{
		final boolean value = get();

		return value ? "1" : "0";
	}

	@Override
	public Fraction getEntitiesPerPixel() { return new Fraction(); }

	@Override
	public void updateIndex( final int index )
	{
		this.i = index;
	}

	@Override
	public int getIndex()
	{
		return i;
	}

	@Override
	public void incIndex()
	{
		++i;
	}

	@Override
	public void incIndex( final int increment )
	{
		i += increment;
	}

	@Override
	public void decIndex()
	{
		--i;
	}

	@Override
	public void decIndex( final int decrement )
	{
		i -= decrement;
	}

	@Override
	public int getBitsPerPixel()
	{
		return 1;
	}

	@Override
	public boolean valueEquals( final NativeBoolType t )
	{
		return get() == t.get();
	}
}
