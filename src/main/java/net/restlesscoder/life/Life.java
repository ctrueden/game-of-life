
package net.restlesscoder.life;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.logic.NativeBoolType;

import org.scijava.command.Command;
import org.scijava.command.Interactive;
import org.scijava.display.DisplayService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;
import org.scijava.widget.Button;
import org.scijava.widget.NumberWidget;

/**
 * Conway's Game of Life.
 * 
 * @author Curtis Rueden
 */
@Plugin(type = Command.class, label = "Conway's Game of Life")
public class Life implements Command, Interactive {

	private static final String FOUR = "4-connected";
	private static final String EIGHT = "8-connected";

	@Parameter
	private DatasetService datasetService;

	@Parameter
	private DisplayService displayService;

	@Parameter
	private UIService uiService;

	@Parameter(label = "Minimum neighbors", min = "0", max = "8")
	private int minNeighbors = 2;
	
	@Parameter(label = "Maximum neighbors", min = "0", max = "8")
	private int maxNeighbors = 3;

	@Parameter(label = "Frames per second", min = "1", max = "100",
		style = NumberWidget.SCROLL_BAR_STYLE)
	private int fps = 10;

	@Parameter(choices = {FOUR, EIGHT})
	private String connectedness = EIGHT;

	@Parameter(label = "Initial saturation % when randomizing", min = "1",
		max = "99", style = NumberWidget.SCROLL_BAR_STYLE)
	private int saturation = 50;

	@Parameter(callback = "randomize")
	private Button randomize;

	@Parameter(callback = "pattern")
	private Button pattern;

	private int w = 64, h = 64;
	private Img<NativeBoolType> field;
	private Dataset dataset;

	/** Buffer for use while recomputing the image. */
	private boolean[] bits = new boolean[w * h];

	// -- Life methods --

	/** Performs one iteration of the game. */
	public void iterate() {
		final boolean corners = connectedness.equals(EIGHT);
		// compute the new bit field
		final RandomAccess<NativeBoolType> ra = field.randomAccess();
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				final int i = y * w + x;
				final int n = neighbors(ra, x, y, corners);
				ra.setPosition(x, 0);
				ra.setPosition(y, 1);
				final boolean alive = ra.get().get();
				if (alive) {
					// Living cell stays alive if between min and max inclusive.
					bits[i] = n >= minNeighbors && n <= maxNeighbors;
				}
				else {
					// Dead cell comes alive only if at exactly max.
					bits[i] = n == maxNeighbors;
				}
			}
		}
		// write the new bit field into the image
		final Cursor<NativeBoolType> cursor = field.localizingCursor();
		while (cursor.hasNext()) {
			cursor.fwd();
			final int x = cursor.getIntPosition(0);
			final int y = cursor.getIntPosition(1);
			final int i = y * w + x;
			cursor.get().set(bits[i]);
		}
		dataset.update();
	}

	/** Randomizes a new bit field. */
	public void randomize() {
		final Cursor<NativeBoolType> cursor = field.localizingCursor();
		final double chance = saturation / 100d;
		while (cursor.hasNext()) {
			cursor.next().set(Math.random() <= chance);
		}
		dataset.update();
	}

	/** Sets the field to an interesting pattern. */
	public void pattern() throws IOException {
		pattern("pulsar.txt");
	}

	// -- Command methods --

	@Override
	public void run() {
		// create a boolean-typed image
		field = ArrayImgs.booleans(w, h);

		// wrap the image in a Dataset, for easy updating
		dataset = datasetService.create(field);
		dataset.setName("Life Simulation");

		// show the image
		uiService.show(dataset);

		// iterate on the game
		while (true) {
			iterate();
			try {
				Thread.sleep(1000 / fps);
			}
			catch (final InterruptedException exc) {
				exc.printStackTrace();
			}
		}
	}

	// -- Main --

	public static void main(final String... args) {
		ImageJ ij = new ImageJ();
		ij.ui().showUI("swing");
		ij.command().run(Life.class, true);
	}

	// -- Helper methods --

	private int neighbors(RandomAccess<NativeBoolType> ra, int x, int y,
		boolean corners)
	{
		int n = 0;
		// four-connected
		n += val(ra, x - 1, y);
		n += val(ra, x + 1, y);
		n += val(ra, x, y - 1);
		n += val(ra, x, y + 1);
		// eight-connected
		if (corners) {
			n += val(ra, x - 1, y - 1);
			n += val(ra, x + 1, y - 1);
			n += val(ra, x - 1, y + 1);
			n += val(ra, x + 1, y + 1);
		}
		return n;
	}

	private int val(RandomAccess<NativeBoolType> ra, int x, int y) {
		if (x < 0 || x >= w || y < 0 || y >= h) return 0;
		ra.setPosition(x, 0);
		ra.setPosition(y, 1);
		return ra.get().get() ? 1 : 0;
	}

	private void pattern(String path) throws IOException {
		org.scijava.util.BoolArray bools = new org.scijava.util.BoolArray();
		int pw = 0;
		try (final InputStream in = getClass().getResourceAsStream(path)) {
			final BufferedReader reader = //
				new BufferedReader(new InputStreamReader(in));
			while (true) {
				final String line = reader.readLine();
				if (line == null) break;
				for (char c : line.toCharArray()) {
					bools.addValue(c == '*');
				}
				if (pw == 0) pw = bools.size();
			}
		}
		int ph = bools.size() / pw;
		int offsetX = (w - pw) / 2;
		int offsetY = (h - ph) / 2;

		final RandomAccess<NativeBoolType> ra = field.randomAccess();
		for (int y = 0; y < ph; y++) {
			for (int x = 0; x < pw; x++) {
				final int i = y * pw + x;
				ra.setPosition(offsetX + x, 0);
				ra.setPosition(offsetY + y, 1);
				ra.get().set(bools.getValue(i));
			}
		}
		dataset.update();
	}
}
