package patientCare;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sim.util.Bag;

public class Tests {
	
	Care care;
	Patient onePatient;
	Provider oneProvider;
	long seed = 19382109;
	
	@Test
	void run_simulation_10_steps() {
		int varsigma = 10; int N = 2; int W = 2;
		long currentSeed = System.currentTimeMillis();
		care = new Care(currentSeed);
		care.setvarsigma(varsigma);
		care.setN(N);
		care.setW(W);
		care.start();
		care.obsSteps = 1;
		String[] arg = new String[1]; arg[0] = "Hola";
		Care.main(arg);
	}
	
	
	@Test
	void d1_disease_after_treatment_1() {
	int varsigma = 500; int N = 1; double delta = 11; int W = 1;
	long currentSeed = System.currentTimeMillis();
	care = new Care(currentSeed);
	care.setvarsigma(varsigma);
	care.setN(N);
	care.setW(W);
	care.start();
	
	care.pat_init.setdelta(care.patients, delta); 
	care.pat_init.setpsi(care.patients, 0);
	care.pat_init.setcapN(care.patients, 1);
	care.prov_init.settau(care.providers,1); 
	care.prov_init.setlambda(care.providers, 1);
	care.prov_init.setalpha(care.providers, N);
	
		//care
		assertEquals(varsigma, care.varsigma, "vasrigma was not set right");
		//patient initializers
		assertEquals(delta, ((Patient)care.patients.objs[0]).delta_p, "delta_p was not set right");
		assertEquals(0, ((Patient)care.patients.objs[0]).psi_p, "psi_p was not set right");
		assertEquals((float)delta/varsigma, ((Patient)care.patients.objs[care.random.nextInt(N)]).progressProbability, "progressProbability was not set right");
		assertTrue(((Patient)care.patients.objs[0]).e_p_i_1[0]>0, "Positive initial expectation needed for this test");
		
		
		//In this scenario, every time a disease progress:
		// The patient expecientes 1 need
		// The patient will seek care (psi = 0, expectations are never =0, and capN = 1)
		// The doctor is always available and prescribe a treatment = 1
		// We test that needs never accumulate; are always treated.
		for(int i = 0; i<varsigma;i++) {
			care.schedule.step(care);
			assertNotEquals(2, ((Patient)care.patients.objs[0]).h_p_i_1, "Helath status must not accumulate in this setting "+ "seed: "+currentSeed);
		}
		care.finish();
	}
	
	@Test
	void d1_disease_after_treatment_2() {
	//long currentSeed = System.currentTimeMillis();
	long currentSeed = System.currentTimeMillis();
	care = new Care(1751891775290L);
	care.start();
	onePatient = ((Patient)care.patients.objs[0]);
	//onePatient.testing = true;
	onePatient.h_p_i_1 = 3;
	onePatient.t_p_i_1 = 1;
	onePatient.diseaseEvolution(care);
	assertEquals(2.0, onePatient.h_p_i - onePatient.Bernoulli, "Disease was not treated in diseaseEvolution"+ "seed: "+currentSeed);
	}

	
	@Test
	void d2_stochastic_disease_progression() {
	int varsigma = 150; int N = 2000; double delta = 11;
	long currentSeed = System.currentTimeMillis();
	care = new Care(currentSeed);
	care.setvarsigma(varsigma);
	care.setN(N); 
	care.obsSteps = 1;
	care.start();
	care.pat_init.setdelta(care.patients, delta); //same disease severity for all
	care.prov_init.settau(care.providers,0); //no treatment
	
		//assigned varsigma
		assertEquals(varsigma, care.varsigma);
		//assigned delta_p
		assertEquals(delta, ((Patient)care.patients.objs[care.random.nextInt(N)]).delta_p);
		assertEquals(delta, ((Patient)care.patients.objs[care.random.nextInt(N)]).delta_p);
		//progress probability
		assertEquals((float)delta/varsigma, ((Patient)care.patients.objs[care.random.nextInt(N)]).progressProbability);

	double result = 0;
		for(int step = 0; step<varsigma; step++) {
		for(int p = 0; p<N;p++) {
			onePatient = ((Patient)care.patients.objs[p]);
			onePatient.diseaseEvolution(care);
			result += onePatient.Bernoulli;
		}
	}

	assertEquals(delta,(int)(result/N+0.5), "Unexpected need evolution (could be random error, re-test)"+ "seed: "+currentSeed); //is approx delta, I'm adding 0.5 to round up
	}
	
	@Test
	void n1_limitNeeds() {
	long currentSeed = System.currentTimeMillis();
	care = new Care(currentSeed);
	care.start();
	((Patient)care.patients.objs[0]).h_p_i = -3;
	((Patient)care.patients.objs[0]).healthNeedPerception();
	assertEquals(0, ((Patient)care.patients.objs[0]).n_p_i, "Need perception waas expected to be floored to 0 (from a negative health status)"+ "seed: "+currentSeed); //is approx delta, I'm adding 0.5 to round up
	((Patient)care.patients.objs[0]).h_p_i = 3;
	((Patient)care.patients.objs[0]).capN_p = 3;
	((Patient)care.patients.objs[0]).healthNeedPerception();
	assertEquals(3, ((Patient)care.patients.objs[0]).n_p_i, "Need perception waas expected to equal health status"+ "seed: "+currentSeed); //is approx delta, I'm adding 0.5 to round up
	((Patient)care.patients.objs[0]).capN_p = 2;
	((Patient)care.patients.objs[0]).healthNeedPerception();
	assertEquals(2, ((Patient)care.patients.objs[0]).n_p_i, "Need perception waas expected to be capped to 2"+ "seed: "+currentSeed); //is approx delta, I'm adding 0.5 to round up
	}
	
	@Test
	void e1_createEarray() {
		int W = 10; int N = 30;
		long currentSeed = System.currentTimeMillis();
		care = new Care(currentSeed);
		care.setW(W);
		care.setN(N);
		care.start();
		assertEquals(W, ((Patient)care.patients.objs[care.random.nextInt(N)]).e_p_i.length, "the e_p arrays should have length W"+ "seed: "+currentSeed);
		assertEquals(W, ((Patient)care.patients.objs[care.random.nextInt(N)]).e_p_i_1.length, "the e_p arrays should have length W"+ "seed: "+currentSeed);

	}
	
	@Test
	void e2_e3_formExpetations() {
		int W = 8; int N = 1;
		long currentSeed = System.currentTimeMillis();
		care = new Care(currentSeed);
		care.setW(W);
		care.setN(N);
		care.start();
		
		care.pat_init.setrho(care.patients,1);
		care.pat_init.seteta(care.patients, 1);
		care.pat_init.setkappa(care.patients,0);


		//Set scenario
		int[] visits    = new int[] {0,1,1,0,0,1,1,0};
		int[] behaviour = new int[] {1,0,1,0,1,0,1,1};
		double[] e_1s = new double[] {2,2,2,2,2,2,2,2};
		((Patient)care.patients.objs[0]).c_p_i_1 = visits;
		((Patient)care.patients.objs[0]).b_p_i_1 = behaviour;
		((Patient)care.patients.objs[0]).e_p_i_1 = e_1s;

		//Form expectations
		((Patient)care.patients.objs[0]).expectationFormation(care);
		
		for (int i=0;i<W;i++) {

		//case 1: saw a provider
		if(((Patient)care.patients.objs[0]).c_p_i_1[i] == 1) {
			assertEquals(3 , ((Patient)care.patients.objs[0]).e_p_i[i], "Should have increased expectations by rho=1 after visitng w"+ "seed: "+currentSeed);
			}
		//case 2 didnt see a seeked provider
		if(((Patient)care.patients.objs[0]).c_p_i_1[i] == 0 & ((Patient)care.patients.objs[0]).b_p_i_1[i] == 1) {
			assertEquals(1 , ((Patient)care.patients.objs[0]).e_p_i[i], "Should have decresed expectations by eta=1 after not seeing w"+ "seed: "+currentSeed);
		}
		//case 3, didn't want and didn't see a provider
		if(((Patient)care.patients.objs[0]).c_p_i_1[i] == 0 & ((Patient)care.patients.objs[0]).b_p_i_1[i] == 0) {
			assertEquals(2 , ((Patient)care.patients.objs[0]).e_p_i[i], "Should have decresed expectations by eta=1 after not seeing w"+ "seed: "+currentSeed);
		}
		
		}
	}
	
	@Test
	void e4_formExpetations() {
		int W = 1; int N = 1;
		long currentSeed = System.currentTimeMillis();
		care = new Care(currentSeed);
		care.setW(W);
		care.setN(N);
		care.start();
		
		care.pat_init.setrho(care.patients,0.5);
		care.pat_init.seteta(care.patients, 0.5);
		care.pat_init.setkappa(care.patients,1);

		int n = 1000;
		double[] sampleFluctutations = new double[n];
		double meanFluctuation =0;
		for(int i=0;i<n;i++) {
			((Patient)care.patients.objs[0]).expectationFormation(care);
			sampleFluctutations[i] = ((Patient)care.patients.objs[0]).e_fluctuation;
			meanFluctuation +=sampleFluctutations[i];
		}
		meanFluctuation = meanFluctuation/n;
		double variance = 0;
		for(int i =0;i<n;i++) {
			variance += (sampleFluctutations[i] - meanFluctuation)*(sampleFluctutations[i] - meanFluctuation);
		}
	}
	
	@Test
	void b1_break_ties_at_random_expectations() {
		care = new Care(seed);
		int N = 1; int W = 10;
		care.setW(W);
		care.setN(N);
		care.start();
		 
		for(int i=0;i<W;i++) {((Patient)care.patients.objs[0]).e_p_i[i] = 2.0;}
		//for(int i=0;i<W;i++) {System.out.println(((Patient)care.patients.objs[0]).e_p_i_1[i]);}
		((Patient)care.patients.objs[0]).behaviouralRule(care);
		assertEquals(2,((Patient)care.patients.objs[0]).wMaxExpectation);
		((Patient)care.patients.objs[0]).behaviouralRule(care);
		assertEquals(0,((Patient)care.patients.objs[0]).wMaxExpectation);
		((Patient)care.patients.objs[0]).e_p_i[3] = 3.0;
		((Patient)care.patients.objs[0]).behaviouralRule(care);
		assertEquals(3,((Patient)care.patients.objs[0]).wMaxExpectation);
		
	}
	
	@Test
	void b2_zero_needs_or_expect() {

		long currentSeed = System.currentTimeMillis();
		currentSeed = 1751874283676L;
		care = new Care(currentSeed);		int N = 1; int W = 10;
		care.setW(W);
		care.setN(N);
		care.start();
		care.pat_init.setcapE(care.patients, 1.0);
		care.pat_init.setcapN(care.patients, 1.0);
		onePatient = (Patient)care.patients.objs[0];
		
		for(int i=0;i<W;i++) {onePatient.e_p_i[i] = 0.0;}
		onePatient.n_p_i = 0.0;
		
		onePatient.behaviouralRule(care);
		for(int i=0;i<W;i++) {assertEquals(0, onePatient.b_p_i[i]);}

		onePatient.n_p_i = 1.0;

		onePatient.behaviouralRule(care);		
		for(int i=0;i<W;i++) {assertEquals(0, onePatient.b_p_i[i]);}
		
		((Patient)care.patients.objs[0]).n_p_i = 1.0;
		onePatient.e_p_i[3] = 1.0; onePatient.e_p_i[7] = 1.0;
		onePatient.psi_p = 1;
		onePatient.testing = true;

		onePatient.behaviouralRule(care);	
		
		//for(int i=0;i<W;i++) {System.out.println(onePatient.b_p_i[i]);}
		assertTrue(onePatient.b_p_i[3] == 1 | onePatient.b_p_i[7] == 1, "Patient should have seek care with w 7 or 3."+ "seed: "+currentSeed);
		
	}
	
	@Test
	void b3_iota_well_specified() {
		long currentSeed = System.currentTimeMillis();
		care = new Care(currentSeed);		int N = 1; int W = 10;
		care.setW(W);
		care.setN(N);
		care.start();
		onePatient = (Patient)care.patients.objs[0];

		double capE = 5.2; double capN = 3.7; double psi= 0.54;
		care.pat_init.setcapE(care.patients, capE);
		care.pat_init.setcapN(care.patients, capN);
		care.pat_init.setpsi(care.patients, psi);
		
		assertEquals(1/(onePatient.psi_p*capE+(1-onePatient.psi_p)*capN), onePatient.iota_p, "iota not well specified");
		capE = 4.2; capN = 0; psi= 0.1;
		care.pat_init.setcapE(care.patients, capE);
		care.pat_init.setcapN(care.patients, capN);
		care.pat_init.setpsi(care.patients, psi);
		
		assertEquals(1/(onePatient.psi_p*capE+(1-onePatient.psi_p)*capN), onePatient.iota_p, "iota not well specified");
	}
	
	@Test
	void effect_of_psi() {
		long currentSeed = System.currentTimeMillis();
		care = new Care(currentSeed);int N = 1; int W = 1;
		care.setW(W);
		care.setN(N);
		care.start();
		onePatient = (Patient)care.patients.objs[0];
		
		double capE = 10; double capN = 10;
		care.pat_init.setcapE(care.patients, capE);
		care.pat_init.setcapN(care.patients, capN);
		
		onePatient.n_p_i = 5;
		onePatient.e_p_i[0] = 3;
		care.pat_init.setpsi(care.patients, 1);
		onePatient.behaviouralRule(care);
		assertEquals((int)(0.3*1000), (int)(onePatient.currentMot*1000), "Psi not working"+ "seed: "+currentSeed); //handles double representation errors
		care.pat_init.setpsi(care.patients, 0.5);
		onePatient.behaviouralRule(care);
		double result = ((0.5*3) + (0.5*5))/10;
		assertEquals((int)(result*1000), (int)(onePatient.currentMot*1000),"Psi not working"+ "seed: "+currentSeed); //handles double representation errors
		care.pat_init.setpsi(care.patients, 0);
		onePatient.behaviouralRule(care);
		assertEquals((int)(0.5*1000), (int)(onePatient.currentMot*1000),"Psi not working"+ "seed: "+currentSeed); //handles double representation errors

	}
	
	@Test
	void p1_compute_minimum() {
		long currentSeed = System.currentTimeMillis();
		double lambda; double tau; double healthStatus;
		care = new Care(currentSeed);int N = 1; int W = 1; int varsigma = 10; double result;
		care.setW(W);
		care.setN(N);
		care.setvarsigma(varsigma);
		care.start();
		oneProvider = (Provider)(care.providers.objs[0]);
		onePatient = (Patient)care.patients.objs[0]; 
		int[] prevInteract = {1,0,0,1,1,1,0,1,1,0};

		lambda = 3; tau = 1; healthStatus = 2;
		care.prov_init.setlambda(care.providers, lambda);
		care.prov_init.settau(care.providers, tau);
		oneProvider.C_w[0] = prevInteract;
		result = oneProvider.interactWithPatient(0, healthStatus);
		assertEquals(tau,result, "Prescription rule not working" + "seed: "+currentSeed);
		
		lambda = 1; tau = 1; healthStatus = 12;
		care.prov_init.setlambda(care.providers, lambda);
		care.prov_init.settau(care.providers, tau);
		oneProvider.C_w[0] = prevInteract;
		result = oneProvider.interactWithPatient(0, healthStatus);
		assertEquals(0.5,result, "Prescription rule not working" + "seed: "+currentSeed);
		
		lambda = 1; tau = 1; healthStatus = 0.2;
		care.prov_init.setlambda(care.providers, lambda);
		care.prov_init.settau(care.providers, tau);
		oneProvider.C_w[0] = prevInteract;
		result = oneProvider.interactWithPatient(0, healthStatus);
		assertEquals(healthStatus,result, "Prescription rule not working" + "seed: "+currentSeed);
	}
	
	@Test
	void p2_counter() {
		long currentSeed = System.currentTimeMillis();
		double lambda; double tau; double healthStatus;
		care = new Care(currentSeed);int N = 1; int W = 1; int varsigma = 5; double result;
		care.setW(W);
		care.setN(N);
		care.setvarsigma(varsigma);
		care.start();
		oneProvider = (Provider)(care.providers.objs[0]);
		int[] prevInteract = {1,0,0,1,0};

		oneProvider.C_w[0] = prevInteract;
		oneProvider.thisweek = 4;
		oneProvider.interactWithPatient(0, 10);
		assertEquals(3, oneProvider.Ccounter, "provider interaction counter not working "+ "seed: "+currentSeed);
			}
	
	@Test
	void p3_proportionalEffec() {
		long currentSeed = System.currentTimeMillis();
		double lambda; double tau; double healthStatus;
		care = new Care(currentSeed);int N = 1; int W = 1; int varsigma = 1; double result;
		care.setW(W);
		care.setN(N);
		care.setvarsigma(varsigma);
		care.start();
		care.prov_init.settau(care.providers, 100);
		oneProvider = (Provider)(care.providers.objs[0]);
		int[] prevInteract = {1};

		oneProvider.C_w[0] = prevInteract;
		oneProvider.thisweek = 0;
		
		lambda = 1;healthStatus=10;
		oneProvider.lambda_w = lambda;
		oneProvider.testing = true;
		result= oneProvider.interactWithPatient(0,healthStatus);
		assertEquals(lambda/healthStatus, result, "provider interaction counter not working "+ "seed: "+currentSeed);
	
		lambda = 2;healthStatus=5;
		oneProvider.lambda_w = lambda;
		oneProvider.testing = true;
		result= oneProvider.interactWithPatient(0,healthStatus);
		assertEquals(lambda/healthStatus, result, "provider interaction counter not working "+ "seed: "+currentSeed);
		
	}
	
	@Test
	void g1_g2__g3_g4interact_max1_only_interact_if_anyB() {
		long currentSeed = System.currentTimeMillis();
		care = new Care(currentSeed);int N = 3000; int W = 50; int varsigma = 150; double result;
		care.setN(N);
		care.setW(W);
		care.setvarsigma(varsigma);
		care.obsSteps = 1;
		care.PATIENT_INIT = "random";
		care.start();
		
		for(int step=0;step<varsigma;step++) {
			care.schedule.step(care);
		}
		
		int[][][] C_p_w_i = care.observer.getC();
		int[][][] B_p_w_i = care.observer.getB();

		int providersForThisPatient = 0;
		int behaviourInThisIteration = 0;
		for(int step=0;step<varsigma;step++) {
		for(int p=0;p<N;p++) { onePatient = (Patient)care.patients.objs[0]; 
			providersForThisPatient = 0;
			behaviourInThisIteration = 0;
			for (int w=0;w<W;w++) {
				providersForThisPatient += C_p_w_i[p][w][step];
				behaviourInThisIteration += B_p_w_i[p][w][step];	
				//that the global representation (from Patient) equals the Provider representation
				if(C_p_w_i[p][w][step] == 1) {
					for(int ww=0;ww<W;ww++) {
						if(((Provider)care.providers.objs[ww]).w == w) {oneProvider = ((Provider)care.providers.objs[ww]);break;}
					}
					assertTrue(oneProvider.C_w[p][step] == C_p_w_i[p][w][step], "Provider and Patient representations are different "+ "seed: "+currentSeed);
				}
			}
			if(providersForThisPatient > 1) {
				assertTrue(behaviourInThisIteration > 0,"a patient interacted with a provider but no behaviour was >0 "+ "seed: "+currentSeed);	
			}
			assertTrue(providersForThisPatient == 0 | providersForThisPatient == 1, "a patient interactions with providers are wrong, are less than 0 or more than 1 in a time step "+ "seed: "+currentSeed);
				}}
		
		int[][][] Providers_C_p_w_i = new int[N][W][varsigma];
		
		int this_W = 0;
		for(int step=0;step<varsigma;step++) {
			for(int p =0;p<N;p++) {
				for(int w=0;w<W;w++) {
					oneProvider = (Provider)care.providers.objs[w];
					this_W = oneProvider.w;
					assertTrue(oneProvider.C_w[p][step] == C_p_w_i[p][this_W][step], "Provider and Patient representations are different "+ "seed: "+currentSeed);
				}	
			}
		}
	}
	

}
