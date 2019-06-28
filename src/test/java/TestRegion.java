import static org.junit.Assert.assertEquals;

import org.junit.Test;

import bot.penguee.Position;
import bot.penguee.Region;

public class TestRegion {

	@Test
	public void testBoundsPosition() {
		int x = 500;
		int y = 700;
		Position mp = new Position(x, y);
		Region mr = new Region(0, 0, 1000, 1000);
		
		assertEquals(mr.bounds(mp), true);
		assertEquals(mr.bounds(x, y), true);
		
		x = 2000;
		y = 2000;
		mp = new Position(x, y);
		
		assertEquals(mr.bounds(mp), false);
		assertEquals(mr.bounds(x, y), false);
	}

	@Test
	public void testBoundsRegion() {
		Region reg1 = new Region(0, 0, 1000, 1000);
		Region reg2 = new Region(400, 400, 800, 800);
		
		assertEquals(reg1.bounds(reg2), true);
		reg2 = new Region(400, 400, 2000, 800);
		assertEquals(reg1.bounds(reg2), false);
	}
		
}
