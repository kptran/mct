/*******************************************************************************
 * Mission Control Technologies, Copyright (c) 2009-2012, United States Government
 * as represented by the Administrator of the National Aeronautics and Space 
 * Administration. All rights reserved.
 *
 * The MCT platform is licensed under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations under 
 * the License.
 *
 * MCT includes source code licensed under additional open source licenses. See 
 * the MCT Open Source Licenses file included with this distribution or the About 
 * MCT Licenses dialog available at runtime from the MCT Help menu for additional 
 * information. 
 *******************************************************************************/
package gov.nasa.arc.mct.service.component;

import gov.nasa.arc.mct.services.internal.component.MCTCountDownLatch;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LatchComponentAccessTest {
	private MCTCountDownLatch latch;
	private LatchComponentAccss latchComponentAccess;

	@BeforeClass
	public void setup() {
		latch = new TestCountDonwLatch(1);
		latchComponentAccess = new LatchComponentAccss();
	}

	@Test
	public void testLatchComponent() throws InterruptedException, BrokenBarrierException {
		final CyclicBarrier barrier = new CyclicBarrier(2);
		final AtomicBoolean testFlag = new AtomicBoolean(false);
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					barrier.await();
				} catch (InterruptedException e) {
					throw new AssertionError(e);
				} catch (BrokenBarrierException e) {					
					throw new AssertionError(e);
				}
				testFlag.set(true);
				latchComponentAccess.setLatch(latch);
			}
		};
		Thread t = new Thread(runnable);
		t.start();
		barrier.await();
		latch.await();
		Assert.assertTrue(testFlag.get());
	}

	private static final class TestCountDonwLatch implements MCTCountDownLatch {
		private final CountDownLatch latch;

		public TestCountDonwLatch(int count) {
			latch = new CountDownLatch(count);
		}

		@Override
		public void await() throws InterruptedException {
			latch.await();
		}

		@Override
		public void countDown() {
			latch.countDown();
		}
	}
}
