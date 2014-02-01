package de.secondsystem.game01.model.collections;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jsfml.graphics.FloatRect;
import org.jsfml.system.Vector2f;
import org.junit.Before;
import org.junit.Test;

import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.collections.ISpatialIndex.EntryWalker;

public class SpatialGridTest {

	private final SpatialGrid<TestEntry> grid = new SpatialGrid<>(100, new Vector2f(-200, -200), new Vector2f(100, 100));
	
	@Before
	public void setUp() throws Exception {
		grid.add(new TestEntry('a', 0, 0, 5));
		grid.add(new TestEntry('b', -200, -100, 30));
		grid.add(new TestEntry('c', 400, 100, 15));
	}

	@Test
	public void testQueryByRect() {
		grid.query(new FloatRect(0, 0, 100, 100), new TestWalker('a'));
		grid.query(new FloatRect(-170, -70, 100, 100), new TestWalker('a', 'b'));
		grid.query(new FloatRect(-170, -70, 400+150, 100+70), new TestWalker('a', 'b', 'c'));
	}

	@Test
	public void testQueryByPoint() {
		// TODO
	}
	
	private static class TestWalker extends EntryWalker<TestEntry> {
		private final Set<Character> expected;
		public TestWalker(Character... exp) {
			expected = new HashSet<>(Arrays.asList(exp));
		}
		@Override
		public void walk(TestEntry entry) {
			assertTrue("Unexpected entry: "+entry, expected.contains(entry.id));
			expected.remove(entry.id);
		}
		@Override
		public void finished() {
			assertTrue("Missing entries: "+expected, expected.isEmpty());
		}
	}
	
	private static class TestEntry extends IndexedMoveable implements IDimensioned {

		private final char id;
		
		private Vector2f pos;
		
		private final float size;
		
		public TestEntry(char id, float x, float y, float size) {
			this.id = id;
			this.pos = new Vector2f(x, y);
			this.size = size;
		}
		
		@Override
		public void setRotation(float degree) {
		}

		@Override
		public float getRotation() {
			return 0;
		}

		@Override
		public Vector2f getPosition() {
			return pos;
		}

		@Override
		public float getHeight() {
			return size;
		}

		@Override
		public float getWidth() {
			return size;
		}

		@Override
		protected void doSetPosition(Vector2f pos) {
			this.pos = pos;
		}
		
		@Override
		public String toString() {
			return "ENTRY-"+id;
		}
	}
}
