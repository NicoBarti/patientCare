package patientCare;
import sim.util.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import sim.util.Bag;

public class Tests {
	
	Care care;
	Patient onePatient;
	Provider oneProvider;
	Provider provider_1;
	Provider provider_2;
	long seed = 19382109;
	// Repeat the test for all policies:
	String[] policies = new String[] {"H_segmented","patient_centred","basal"};


	
	@Test
	void d1_disease_after_treatment_1() {
	for(int policy = 0; policy < policies.length; policy++) {
		int varsigma = 500; int N = 1; double delta = 11; int W = 1;
		long currentSeed = System.currentTimeMillis();
		care = new Care(1751958692214L);
		care.setvarsigma(varsigma);
		care.setN(N);
		care.setW(W);
		care.setPi(policies[policy]);
		care.PATIENT_INIT = "random-basal";
		care.start();
		care.startObserver();
		care.order = new long[N];
		care.H_at_Order = new double[N];
		care.NE_at_Order= new double[N];

		care.pat_init.setdelta(care.patients, delta); 
		care.pat_init.setpsi(care.patients, 0);
		care.pat_init.setcapN(care.patients, 1);
		care.pat_init.setkappa(care.patients,0);

		care.prov_init.settau(care.providers,1); 
		care.prov_init.setlambda(care.providers, 1);
		care.prov_init.setalpha(care.providers, N);
			//care
			assertEquals(varsigma, care.varsigma, "vasrigma was not set right");
			//patient initializers
			assertEquals(delta, ((Patient)care.patients.objs[0]).delta_p, "delta_p was not set right");
			assertEquals(0, ((Patient)care.patients.objs[0]).psi_p, "psi_p was not set right");
			assertEquals((float)delta/52, ((Patient)care.patients.objs[care.random.nextInt(N)]).progressProbability, "progressProbability was not set right");
			assertTrue(((Patient)care.patients.objs[0]).e_p_i_1[0]>0, "Positive initial expectation needed for this test");
			
			
			//In this scenario, every time a disease progress:
			// The patient expecientes 1 need
			// The patient will seek care (psi = 0, expectations are never =0, and capN = 1)
			// The doctor is always available and prescribe a treatment = 1
			// We test that needs never accumulate; are always treated.
			((Patient)care.patients.objs[0]).testing = true;
			((Provider)care.providers.objs[0]).testing = true;
			for(int i = 0; i<varsigma;i++) {
				care.schedule.step(care);
				assertNotEquals(2, ((Patient)care.patients.objs[0]).h_p_i_1, "Helath status must not accumulate in this setting "+ "seed: "+currentSeed);
			}
			care.finish();
	}
	
	}
	
	@Test
	void d1_disease_after_treatment_2() {
	for(int policy = 0; policy < policies.length; policy++) {
		//long currentSeed = System.currentTimeMillis();
		long currentSeed = System.currentTimeMillis();
		care = new Care(1751891775290L);
		care.setPi(policies[policy]);
		care.start();
		onePatient = ((Patient)care.patients.objs[0]);
		//onePatient.testing = true;
		onePatient.h_p_i_1 = 3;
		onePatient.t_p_i_1 = 1;
		onePatient.diseaseEvolution(care);
		assertEquals(2.0, onePatient.h_p_i - onePatient.Bernoulli, "Disease was not treated in diseaseEvolution"+ "seed: "+currentSeed);

	}

		}

	@Test
	void d2_stochastic_disease_progression() {
	for(int policy = 0; policy < policies.length; policy++) {
		int varsigma = 300; int N = 2000; double delta = 5;
		long currentSeed = System.currentTimeMillis();
		care = new Care(currentSeed);
		care.setvarsigma(varsigma);
		care.setN(N); 
		care.setPi(policies[policy]);
		care.OBS_PERIOD = 1;
		care.start();
		care.pat_init.setdelta(care.patients, delta); //same disease severity for all
		care.prov_init.settau(care.providers,0); //no treatment
		
			//assigned varsigma
			assertEquals(varsigma, care.varsigma);
			//assigned delta_p
			assertEquals(delta, ((Patient)care.patients.objs[care.random.nextInt(N)]).delta_p);
			assertEquals(delta, ((Patient)care.patients.objs[care.random.nextInt(N)]).delta_p);
			//progress probability
			assertEquals((float)delta/52, ((Patient)care.patients.objs[care.random.nextInt(N)]).progressProbability);

		double result = 0;
			for(int step = 0; step<varsigma; step++) {
			for(int p = 0; p<N;p++) {
				onePatient = ((Patient)care.patients.objs[p]);
				onePatient.diseaseEvolution(care);
				result += onePatient.Bernoulli;
			}
		}
		//assertEquals((int)(delta*300/52),(int)(result/N+0.5), "Unexpected need evolution (could be random error, re-test)"+ "seed: "+currentSeed); //is approx delta, I'm adding 0.5 to round up	
	}
	}
	
	@Test
	void n1_limitNeeds() {
	for(int policy = 0; policy < policies.length; policy++) {
		long currentSeed = System.currentTimeMillis();
		care = new Care(currentSeed);
		care.setPi(policies[policy]);
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
	}
	
	@Test
	void e1_createEarray() {
	for(int policy = 0; policy < policies.length; policy++) {
		int W = 10; int N = 30;
		long currentSeed = System.currentTimeMillis();
		care = new Care(currentSeed);
		care.setW(W);
		care.setPi(policies[policy]);
		care.setN(N);
		care.start();
		assertEquals(W, ((Patient)care.patients.objs[care.random.nextInt(N)]).e_p_i.length, "the e_p arrays should have length W"+ "seed: "+currentSeed);
		assertEquals(W, ((Patient)care.patients.objs[care.random.nextInt(N)]).e_p_i_1.length, "the e_p arrays should have length W"+ "seed: "+currentSeed);
	}
	}
	
	@Test
	void e2_e3_formExpetations() {
	for(int policy = 0; policy < policies.length; policy++) {
		int W = 8; int N = 1;
		long currentSeed = System.currentTimeMillis();
		care = new Care(currentSeed);
		care.setW(W);
		care.setPi(policies[policy]);
		care.setN(N);
		care.start();
		
		
		care.pat_init.setrho(care.patients,1);
		care.pat_init.seteta(care.patients, 1);
		care.pat_init.setkappa(care.patients,0);
		care.pat_init.setcapE(care.patients, 10);


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
	}
	
	@Test
	void e4_formExpetations() {
	for(int policy = 0; policy < policies.length; policy++) {
		int W = 10; int N = 100;
		Patient patient;
		long currentSeed = System.currentTimeMillis();
		care = new Care(currentSeed);
		care.setW(W);
		care.setN(N);
		care.setPi(policies[policy]);
		care.start();
		
		for (int i =0; i<100;i++) {
			care.schedule.step(care);
			for (int p =0; p< care.patients.numObjs; p++) {
				patient = (Patient)care.patients.objs[p];
				for (int w=0; w< care.W; w++) {
					assertTrue(patient.e_p_i[w] <= patient.capE_p, "capE_p not effective. capE: "+patient.capE_p + " E: "+patient.e_p_i[w]+" seed "+currentSeed);
				}
			}
		}
	}
	}
	
	@Test
	void b1_break_ties_at_random_expectations() {
	for(int policy = 0; policy < policies.length; policy++) {
		long currentSeed = System.currentTimeMillis();
		care = new Care(currentSeed);
		int N = 1; int W = 1000;
		care.setW(W);
		care.setPi(policies[policy]);
		care.setN(N);
		care.start();
		
		Patient onePatient = (Patient)(care.patients.objs[0]);
		
		for(int i=0;i<W;i++) {onePatient.e_p_i[i] = 2.0;}
		int[] ws = new int[2];
		int matchs = 0;
		onePatient.behaviouralRule(care);
		ws[0] = onePatient.wMaxExpectation;
		for(int i = 1; i<ws.length;i++) { 
			onePatient.behaviouralRule(care);
			ws[i] = onePatient.wMaxExpectation;
			if(ws[0]==ws[i]) {matchs+=1;} }
		assertTrue((matchs <1), "Check break ties expectations. The probability of this match is <1/1000 "+ "seed: "+currentSeed);
		onePatient.e_p_i[3] = 3.0;
		onePatient.behaviouralRule(care);
		assertEquals(3,((Patient)care.patients.objs[0]).wMaxExpectation);
	}	
	}
	
	@Test
	void b2_zero_needs_or_expect() {
	for(int policy = 0; policy < policies.length; policy++) {
		long currentSeed = System.currentTimeMillis();
		currentSeed = 1751874283676L;
		care = new Care(currentSeed);		int N = 1; int W = 10;
		care.setW(W);
		care.setPi(policies[policy]);
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
	}
	
	@Test
	void b3_iota_well_specified() {
	for(int policy = 0; policy < policies.length; policy++) {
		long currentSeed = System.currentTimeMillis();
		care = new Care(currentSeed);		int N = 1; int W = 10;
		care.setW(W);
		care.setPi(policies[policy]);
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
			}
	
	@Test
	void effect_of_psi() {
	for(int policy = 0; policy < policies.length; policy++) {
		long currentSeed = System.currentTimeMillis();
		care = new Care(currentSeed);int N = 1; int W = 1;
		care.setW(W);
		care.setPi(policies[policy]);
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
	}
	
	@Test
	void p1_compute_minimum() {
	for(int policy = 0; policy < policies.length; policy++) {
		long currentSeed = System.currentTimeMillis();
		double lambda; double tau; double healthStatus;
		care = new Care(currentSeed);int N = 1; int W = 1; int varsigma = 10; double result;
		care.setW(W);
		care.setPi(policies[policy]);
		care.setN(N);
		care.setvarsigma(varsigma);
		care.start();
		oneProvider = (Provider)(care.providers.objs[0]);
		onePatient = (Patient)care.patients.objs[0]; 
		int prevInteract = 6;

		lambda = 3; tau = 1; healthStatus = 2;
		care.prov_init.setlambda(care.providers, lambda);
		care.prov_init.settau(care.providers, tau);
		oneProvider.SumC_w[0] = prevInteract;
		result = oneProvider.interactWithPatient(0, healthStatus);
		assertEquals(tau,result, "Prescription rule not working" + "seed: "+currentSeed);
		
		lambda = 1; tau = 1; healthStatus = 12;
		care.prov_init.setlambda(care.providers, lambda);
		care.prov_init.settau(care.providers, tau);
		oneProvider.SumC_w[0] = prevInteract-1;
		result = oneProvider.interactWithPatient(0, healthStatus);
		assertEquals(0.5,result, "Prescription rule not working" + "seed: "+currentSeed);
		
		lambda = 1; tau = 1; healthStatus = 0.2;
		care.prov_init.setlambda(care.providers, lambda);
		care.prov_init.settau(care.providers, tau);
		oneProvider.SumC_w[0] = prevInteract;
		result = oneProvider.interactWithPatient(0, healthStatus);
		assertEquals(healthStatus,result, "Prescription rule not working" + "seed: "+currentSeed);
	}
	}
	
	@Test
	void p2_counter() {
	for(int policy = 0; policy < policies.length; policy++) {
		long currentSeed = System.currentTimeMillis();
		double lambda; double tau; double healthStatus;
		care = new Care(currentSeed);int N = 1; int W = 1; int varsigma = 5; double result;
		care.setW(W);
		care.setPi(policies[policy]);
		care.setN(N);
		care.setvarsigma(varsigma);
		care.start();
		oneProvider = (Provider)(care.providers.objs[0]);
		oneProvider.alpha_w =10;
		
		care.appointer.appoint(0, 0, .5);
		care.appointer.appoint(0, 0, .5);
		care.appointer.appoint(0, 0, .5);

		assertEquals(3, oneProvider.SumC_w[0], "provider interaction counter not working "+ "seed: "+currentSeed);

	}
	}
	
	@Test
	void p3_proportionalEffec() {
	for(int policy = 0; policy < policies.length; policy++) {
		long currentSeed = System.currentTimeMillis();
		double lambda; double tau; double healthStatus;
		care = new Care(currentSeed);int N = 1; int W = 1; int varsigma = 1; double result;
		care.setW(W);
		care.setPi(policies[policy]);

		care.setN(N);
		care.setvarsigma(varsigma);
		care.start();
		care.prov_init.settau(care.providers, 100);
		oneProvider = (Provider)(care.providers.objs[0]);
		int prevInteract = 0;

		oneProvider.SumC_w[0] = prevInteract;
		
		lambda = 1;healthStatus=10;
		oneProvider.lambda_w = lambda;
		oneProvider.testing = true;
		result= oneProvider.interactWithPatient(0,healthStatus);
		assertEquals(lambda/healthStatus, result, "provider interaction counter not working "+ "seed: "+currentSeed);
	
		lambda = 2;healthStatus=5;
		oneProvider.SumC_w[0] = prevInteract;
		oneProvider.lambda_w = lambda;
		oneProvider.testing = true;
		result= oneProvider.interactWithPatient(0,healthStatus);
		assertEquals(lambda/healthStatus, result, "provider interaction counter not working "+ "seed: "+currentSeed);
	}
	}
	
	@Test
	void g1_g2__g3_g4interact_max1_only_interact_if_anyB() {
	for(int policy = 0; policy < policies.length; policy++) {
		long currentSeed = System.currentTimeMillis();
		currentSeed = 1753678383484L; // PENDING DEBUG
		care = new Care(currentSeed);
		int N = 3000 + care.random.nextInt(10); 
		int W = 45 + care.random.nextInt(10); 
		care.setPi(policies[policy]);
		int varsigma = 145 + care.random.nextInt(10); 
		care.setN(N);
		care.setW(W);
		care.setvarsigma(varsigma);
		care.OBS_PERIOD = 1;
		care.PATIENT_INIT = "random";
		care.start();
		care.order = new long[N];
		care.H_at_Order = new double[N];
		care.NE_at_Order= new double[N];

		care.pat_init.settesting(care.patients, true);
		care.prov_init.settesting(care.providers, true);
		care.startObserver(false, false, true, false, false, true, false, false, false);


		for(int step=0;step<varsigma;step++) {
			care.schedule.step(care);
		}
		care.finish();

		int[][][] C_p_w_i = care.observer.getC();
		int[][][] B_p_w_i = care.observer.getB();
		
		int providersForThisPatient = 0;
		int behaviourInThisIteration = 0;
		for(int step=0;step<C_p_w_i[0][0].length;step++) {
		for(int p=0;p<N;p++) { onePatient = (Patient)care.patients.objs[0]; 
			providersForThisPatient = 0;
			behaviourInThisIteration = 0;
			for (int w=0;w<W;w++) {
				providersForThisPatient += C_p_w_i[p][w][step];
				behaviourInThisIteration += B_p_w_i[p][w][step];	
			}
			if(providersForThisPatient > 1) {
				assertTrue(behaviourInThisIteration > 0,"a patient interacted with a provider but no behaviour was >0 "+ "seed: "+currentSeed);	
			}
			assertTrue(providersForThisPatient == 0 | providersForThisPatient == 1, "a patient interactions with providers are wrong, are less than 0 or more than 1 in a time step "+ "seed: "+currentSeed);
				}}
		
		//int[][][] Providers_C_p_w_i = new int[N][W][varsigma];
		
		//That the global representation extracted from patient matches the one in the provider
		// seed: 1753678383484 ==> expected: <true> but was: <false>

		int this_W = 0;
		int globalCW = 0;

			for(int p =0;p<N;p++) {
				for(int w=0;w<W;w++) {
					oneProvider = (Provider)care.providers.objs[w];
					this_W = oneProvider.w;
					for(int i =0;i<C_p_w_i[0][0].length;i++) {
						globalCW += C_p_w_i[p][this_W][i];
					}

					assertTrue(oneProvider.SumC_w[p] == globalCW, "Provider and Patient representations are different "+ "seed: "+currentSeed);
					globalCW = 0;
				}	
			}
	}
	}
	
	@Test
	void check_basalPolicy() {
		long currentSeed = System.currentTimeMillis();
		double lambda; double tau; double healthStatus;
		care = new Care(currentSeed);int N = 100; int W = 4; int varsigma = 1000; 
		care.setPi("basal");
		care.setW(W);
		care.setN(N);
		care.setvarsigma(varsigma);
		care.start();
		care.order = new long[N];
		care.H_at_Order = new double[N];
		care.NE_at_Order= new double[N];

		care.pat_init.settesting(care.patients, true);

		
		int patient1 = care.random.nextInt(N);
		int patient2 = 0 ;
		while (patient2 == 0) {
			patient2 = care.random.nextInt(patient1);
		}
		int[] order1_more_order2 = new int[varsigma];
		
		for(int i=0; i< varsigma; i++) {
			care.schedule.step(care);
			//System.out.println("patient1 "+ care.order[patient1] + " / patient2 "+ care.order[patient2]);
			if(care.order[patient1] > care.order[patient2]) {
				order1_more_order2[i] = 1;
			} else {order1_more_order2[i] = 0;} 
		}
		
		
		double prop = 0;
		for (int o = 0; o < order1_more_order2.length;o++) {
			prop+=order1_more_order2[o];
		}
		
		assertTrue(prop/order1_more_order2.length<0.55 & prop/order1_more_order2.length>0.45, 
				"Patient order is unlikely to be random. Patient 1 came after Patient 2 " +
		prop/order1_more_order2.length + " of the time with seed "+currentSeed);
		
	}
	
	@Test
	void check_health_segmentation() {
		long currentSeed = System.currentTimeMillis();
		double lambda; double tau; double healthStatus;
		care = new Care(currentSeed);int N = 100; int W = 4; int varsigma = 1000; 
		//care.setPi("basal");
		care.setW(W);
		care.setN(N);
		care.setPi("H_segmented");
		care.setvarsigma(varsigma);
		care.PATIENT_INIT = "basal";
		care.start();
		care.order = new long[N];
		care.H_at_Order = new double[N];
		care.NE_at_Order= new double[N];

		care.pat_init.settesting(care.patients, true);
		
		for(int i=0; i< varsigma; i++) {
			care.schedule.step(care);
			
			for (int p = 0 ; p< care.patients.numObjs-1; p++) {
				//if(patient_p.testing_get_previousH() == patient_p1.testing_get_previousH()) 
				if(care.H_at_Order[p] == care.H_at_Order[p+1])
				{
					continue;
				}
				if(care.order[p] > care.order[p+1]) { // p was schedduled with less priority that p+1
					//System.out.println("(before) patient "+p +" has order "+care.order[p] + " and healh " + care.H_at_Order[p]);
					//System.out.println("(before) patient " +p +"+1 has order "+care.order[p+1]+" and health "+ care.H_at_Order[p+1]);
					assertTrue( care.H_at_Order[p] < care.H_at_Order[p+1], "Patient "+p+" had health "+care.H_at_Order[p]+
							" but came before the consecutive patient that had health " +care.H_at_Order[p+1]);

				}
				if(care.order[p] < care.order[p+1]) { // p was schedduled with more priority that p+1
					//System.out.println("(after) patient " + p + " has order "+care.order[p] + " and healh " + care.H_at_Order[p]);
					//System.out.println("(after) patient " + p + " +1 has order "+care.order[p+1]+" and health "+ care.H_at_Order[p+1]);
					assertTrue( care.H_at_Order[p] > care.H_at_Order[p+1], "Patient "+p+" has health "+care.H_at_Order[p]+
							" but came after patient the consecutive patient that has health " +care.H_at_Order[p+1]);
				}
				
			}

		}

	}
	
	@Test
	public void correctAppointments() {
		//Check that i get the provider I want if there is capacity
		//Check i don't get the provider because it ran out of appointments
		//Check i don't gent the provider because it is gone
		//Patient gets notified that provider no longer exists
		
		//Start the system
		long currentSeed = System.currentTimeMillis();
		care = new Care(currentSeed); 
		care.N=20; care.W=7;care.varsigma=100;
		care.OBS_PERIOD = 1;
		care.totalCapacity = 130; //unconstrained capacity
		care.start();
		care.startObserver();
		care.pat_init.setdelta(care.patients, 10.0);
		care.pat_init.setcapE(care.patients, 10);
		care.pat_init.setexpectations(care.patients, 10);

		
		//Check that total capacity was assigned correctly
		int currentCapacity = 0;
		for (int p =0; p<care.providers.numObjs; p++) {
			currentCapacity+=((Provider)care.providers.get(p)).A_w;
		}
		assertEquals(130, currentCapacity, "Capacity not assigned correctly");

		//Run it for 10 steps
		for (int i=0;i<10;i++) {
			care.schedule.step(care);

		}
		
		//Check that i get the provider I want if there is capacity
		int pro;
		for(int i=0;i<10;i++) {
		pro = care.random.nextInt(7);
		int ow = 0;
		for(int w=0;w<care.providers.numObjs;w++) {
			if(((Provider)care.providers.get(w)).w == pro) {
				ow = w;
				break;
			}
		}
		assertEquals(pro, care.appointer.appoint(pro, 5, 6)[0], "Didn't get right provider. Providers A_w was enough: "+((Provider)care.providers.get(ow)).A_w);}
	
		//Check i don't get the provider because it ran out of appointments
		for(int i=0; i<5; i++) {
		pro = care.random.nextInt(7);
		int ow = ((Provider)care.providers.get(pro)).w;
		((Provider)care.providers.get(pro)).A_w = 0; ((Provider)care.providers.get(pro)).alpha_w = 0;
		assertTrue(care.appointer.appoint(ow, 0, 1)[0] != ow, "Should not have got an appointment with unavailable provider "+ow);}
		
		//Check i don't get the provider because it is gone
		care.change_W_midwaytrhough(3);
			//find a gone provider
			int goneProdiver = 0;
			boolean found;
			for(int w=0;w<7;w++) {
				goneProdiver = w; // candidate for gone
				found = true;
				for(int ow=0; ow<care.providers.numObjs; ow++) {
					if(((Provider)care.providers.get(ow)).w == w) {
						found = false;
						break;}}
				if(found) {
				break;} //if you reached this point you found it}
			}
		int patId = care.random.nextInt(20);
			//find patient
			Patient patient = null;
			for(int i=0;i<care.patients.numObjs;i++) {
				if(((Patient)care.patients.get(i)).p == patId) {
					patient = ((Patient)care.patients.get(i));
				}
			}
			//Patients expectatios for goneProvider are positive:
			assertTrue(patient.e_p_i_1[goneProdiver] >0, "In this setting the expectations should be high");
			//Patient' can't interact with goneProvider and learns that they are gone
			assertTrue(care.appointer.appoint(goneProdiver, patId, 5)[0] != goneProdiver, "Should not have got an appointment with gone provider "+goneProdiver);
			//Patient gets notified that provider no longer exists, so its expectations are 0
			assertTrue(patient.e_p_i_1[goneProdiver] ==0, "Provider no longer exists, so its expectations should be 0");

	}


	@Test
	void check_patient_centred() {

		long currentSeed = System.currentTimeMillis();
		care = new Care(currentSeed);int N = 100; int W = 4; int varsigma = 1000; 
		//care.setPi("basal");
		care.setW(W);
		care.setN(N);
		care.setPi("patient_centred");
		care.setvarsigma(varsigma);
		care.PATIENT_INIT = "basal";
		care.start();
		care.order = new long[N];
		care.H_at_Order = new double[N];
		care.NE_at_Order= new double[N];
		care.pat_init.settesting(care.patients, true);
		
		for(int i=0; i< varsigma; i++) {
			care.patientOrder=0;
			care.schedule.step(care);
			
			for (int p = 0 ; p< care.patients.numObjs-1; p++) {
				//Compare order up to two decimal places of N-E
				if(Math.round(care.NE_at_Order[p]*100)/100 == Math.round(care.NE_at_Order[p+1]*100)/100)
				{	continue;}
				//p +1 higher priority:
				if(care.order[p] > care.order[p+1]) { // p was schedduled with less priority that p+1
					//System.out.println("(before) patient "+p +" has order "+care.order[p] + " and need-expect " + care.NE_at_Order[p]);
					//System.out.println("(before) patient " +p +"+1 has order "+care.order[p+1]+" and need-expect "+ care.NE_at_Order[p+1]);
					assertTrue( care.NE_at_Order[p+1] > care.NE_at_Order[p] , "Patient "+p+" had need-expect "+care.NE_at_Order[p]+
							" but came before the consecutive patient that had need expect " +care.NE_at_Order[p+1]+" seed "+ currentSeed);
				}
				// p higher priority
				if(care.order[p] < care.order[p+1]) { // p was schedduled with more priority that p+1
					//System.out.println("(after) patient " + p + " has order "+care.order[p] + " and need-expect " + care.NE_at_Order[p]);
					//System.out.println("(after) patient " + p + " +1 has order "+care.order[p+1]+" and need-expect "+ care.NE_at_Order[p+1]);
					assertTrue( care.NE_at_Order[p] > care.NE_at_Order[p+1], "Patient "+p+" has need expect "+care.NE_at_Order[p]+
							" but came after patient the consecutive patient that has need expect " +care.NE_at_Order[p+1]+" seed "+currentSeed);
				}
			}
		}
	}

	@Test
	void check_observer_visitsCounter() {
		long currentSeed = System.currentTimeMillis();
		care = new Care(currentSeed);int N = 200; int W = 30; int varsigma = 500; 
		care.startObserver(false, false, false, false, false, false, true, false, false);
		care.start();
		int sum;
		int[] internalC = new int[W];
		int[][] trackC = new int[N][W];
		int[][] observerC = new int[N][W];
		for(int i=0; i < varsigma; i++) {
			care.schedule.step(care);
			for (int p = 0; p<care.patients.numObjs;p++) {
				onePatient = (Patient)care.patients.objs[p];
				internalC = onePatient.c_p_i_1;
				sum = 0;
				for (int w = 0;w<care.W;w++) {
					//trackC[p][w] = internalC[w]; //TODO test the detailed array, I'm missing the windows here. Maybe capture the whole and then only compare windows
					sum = sum + internalC[w];
				}
				assertTrue(sum<2, "a patient can't have more than 1 interaction per step. Seed: "+currentSeed);
			}
		}
		observerC = care.observer.getSimpleC();
		
		//assertEquals(observerC, trackC, "arrays differ. Seed: "+currentSeed);	
	}
	
	
	@Test
	public void change_N_fromCare_allEffects() {
		//Start the system
		long currentSeed = System.currentTimeMillis();
		care = new Care(currentSeed); 
		care.N=20; care.W=5;care.varsigma=100;
		care.OBS_PERIOD = 1;
		care.start();
		care.startObserver();
		care.pat_init.setdelta(care.patients, 10.0);
		//Run it for 50 steps
		for (int i=0;i<50;i++) {
			care.schedule.step(care);

		}
		//Check the number of patients being observed
		assertEquals(care.observer.H_p_i.length, 20);
		double oneH = care.observer.H_p_i[15][40];
		
		//assign a value to check that will be preserved
		int pat = care.random.nextInt(20);
		int pro = care.random.nextInt(5);
		int loc = care.random.nextInt(10);
		care.observer.H_p_i[pat][loc] = 1772;
		care.observer.N_p_i[pat][loc] = 7422;
		care.observer.T_p_i[pat][loc] = 845;
		care.observer.B_p_w_i[pat][pro][loc] = 6553200;
		care.observer.C_p_w_i[pat][pro][loc] = 8200;
		care.observer.E_p_w_i[pat][pro][loc] = 1298;
		care.observer.simple_C_p_i[pat][loc] = 713;
		care.observer.simple_E_p_i[pat][loc] = 573;
		care.observer.simple_B_p_i[pat][loc] = 631;
		
		// INCRESED N:
		care.change_N_midwaytrhough(30);
		assertEquals(care.patients.numObjs, 30);
		assertEquals(care.observer.H_p_i.length,30);
			
			//Check observer 
				assertEquals(30, care.observer.H_p_i.length);
				assertEquals(30, care.observer.N_p_i.length);
				assertEquals(30, care.observer.C_p_w_i.length);
				assertEquals(30, care.observer.T_p_i.length);
				assertEquals(30, care.observer.E_p_w_i.length);
				assertEquals(30, care.observer.B_p_w_i.length);
				assertEquals(30, care.observer.simple_C_p_i.length);
				assertEquals(30, care.observer.simple_E_p_i.length);
				assertEquals(30, care.observer.simple_B_p_i.length);

			//Info preserved
				assertEquals(1772, care.observer.H_p_i[pat][loc]);
				assertEquals(7422, care.observer.N_p_i[pat][loc]);
				assertEquals(845, care.observer.T_p_i[pat][loc]);
				assertEquals(6553200, care.observer.B_p_w_i[pat][pro][loc]);
				assertEquals(8200, care.observer.C_p_w_i[pat][pro][loc]);
				assertEquals(1298, care.observer.E_p_w_i[pat][pro][loc]);
				assertEquals(713, care.observer.simple_C_p_i[pat][loc]);
				assertEquals(573, care.observer.simple_E_p_i[pat][loc]);
				assertEquals(631, care.observer.simple_B_p_i[pat][loc]);

		int[] patientsIds = new int[30];
		for(int i=0;i<30;i++) {
			patientsIds[((Patient)care.patients.get(i)).p] = ((Patient)care.patients.get(i)).p;
		}
		assertEquals(patientsIds[10], 10, "Failed patient id");
		assertEquals(patientsIds[25], 25, "Failed patient id");
		assertEquals(care.observer.H_p_i[15][40], oneH);
		for(int p =0; p< care.providers.numObjs;p++) {
			assertEquals(((Provider)care.providers.get(p)).SumC_w.length, 30);
		}
		for (int i=50;i<80;i++) {
			care.schedule.step(care);

		}
		// DECREASED N
		care.change_N_midwaytrhough(20);
		
		//check that only 20 patients are in the bag
		assertEquals(20, care.patients.numObjs);
		
		//Check observer 
		assertEquals(30, care.observer.H_p_i.length);
		assertEquals(30, care.observer.N_p_i.length);
		assertEquals(30, care.observer.C_p_w_i.length);
		assertEquals(30, care.observer.T_p_i.length);
		assertEquals(30, care.observer.E_p_w_i.length);
		assertEquals(30, care.observer.B_p_w_i.length);
		assertEquals(30, care.observer.simple_C_p_i.length);
		assertEquals(30, care.observer.simple_E_p_i.length);
		assertEquals(30, care.observer.simple_B_p_i.length);
		
		//Info preserved
		assertEquals(1772, care.observer.H_p_i[pat][loc]);
		assertEquals(7422, care.observer.N_p_i[pat][loc]);
		assertEquals(845, care.observer.T_p_i[pat][loc]);
		assertEquals(6553200, care.observer.B_p_w_i[pat][pro][loc]);
		assertEquals(8200, care.observer.C_p_w_i[pat][pro][loc]);
		assertEquals(1298, care.observer.E_p_w_i[pat][pro][loc]);
		assertEquals(713, care.observer.simple_C_p_i[pat][loc]);
		assertEquals(573, care.observer.simple_E_p_i[pat][loc]);
		assertEquals(631, care.observer.simple_B_p_i[pat][loc]);
		
		//still observers are 30
		for (int i=80;i<90;i++) {
			care.schedule.step(care);

		}
		int minusOnecounter = 0;
		for(int i=0;i<30;i++) {
			if(care.observer.H_p_i[i][85] == -1) {minusOnecounter+=1;
			}
		}
		assertEquals(10,minusOnecounter, "X_i arrays should record -1 when patient is gone");
		
		//increase N again
		care.change_N_midwaytrhough(35);
		assertEquals(35, care.patients.numObjs);

		System.out.println("These are the IDs:");
		for(int i = 0; i< care.patients.numObjs;i++) {		
			System.out.print(" "+((Patient)care.patients.get(i)).p);
			
		}
		
		//Check observer 
		assertEquals(35, care.observer.H_p_i.length);
		assertEquals(35, care.observer.N_p_i.length);
		assertEquals(35, care.observer.C_p_w_i.length);
		assertEquals(35, care.observer.T_p_i.length);
		assertEquals(35, care.observer.E_p_w_i.length);
		assertEquals(35, care.observer.B_p_w_i.length);
		assertEquals(35, care.observer.simple_C_p_i.length);
		assertEquals(35, care.observer.simple_E_p_i.length);
		assertEquals(35, care.observer.simple_B_p_i.length);
		
		//check ID is not reused

		//Check past info is preserved
		
		//Info preserved
		assertEquals(1772, care.observer.H_p_i[pat][loc]);
		assertEquals(7422, care.observer.N_p_i[pat][loc]);
		assertEquals(845, care.observer.T_p_i[pat][loc]);
		assertEquals(6553200, care.observer.B_p_w_i[pat][pro][loc]);
		assertEquals(8200, care.observer.C_p_w_i[pat][pro][loc]);
		assertEquals(1298, care.observer.E_p_w_i[pat][pro][loc]);
		assertEquals(713, care.observer.simple_C_p_i[pat][loc]);
		assertEquals(573, care.observer.simple_E_p_i[pat][loc]);
		assertEquals(631, care.observer.simple_B_p_i[pat][loc]);
		
		
		
		for (int i=90;i<100;i++) {
			care.schedule.step(care);

		}
		
	
		care.finish();
		//check final statistics
		for(int i=0;i<30;i++) {
			//fix the results:
			if(care.observer.H_p_i[i][care.observer.getarraysLengthreturn()-1] != -1) {
				care.observer.H_p_i[i][care.observer.getarraysLengthreturn()-1] = 14;
			}
		}
		assertEquals(14, care.observer.getMeanFinalH());
		assertEquals(0, care.observer.getVarianceFinalH());
		

		
		
	}
	
	@Test
	void test_ChangeInW_effects() {
		//Check that arrays change right in observers witout loosing information and preserving order even after
		//providers ceased to exist. The provider id (.w) is unique trhoughout the simulation.
		//Start the system
		long currentSeed = System.currentTimeMillis();
		care = new Care(currentSeed); 
		care.N=20; care.W=5;care.varsigma=100;
		care.OBS_PERIOD = 1;
		care.totalCapacity = 51; //unconstrained capacity
		care.start();
		care.startObserver();
		care.pat_init.setdelta(care.patients, 10.0);
		
		//Check that total capacity was assigned correctly
		int currentCapacity = 0;
		for (int p =0; p<care.providers.numObjs; p++) {
			currentCapacity+=((Provider)care.providers.get(p)).A_w;
		}
		assertEquals(51, currentCapacity, "Capacity not assigned correctly");

		
		//Run it for 10 steps
		for (int i=0;i<10;i++) {
			care.schedule.step(care);

		}
		//Check the number of providers in the Bag
		assertEquals(5, care.providers.numObjs);
		//Check patient's arrays to providers
		assertEquals(5, ((Patient)care.patients.get(care.random.nextInt(20))).e_p_i_1.length);
		assertEquals(5, ((Patient)care.patients.get(care.random.nextInt(20))).e_p_i.length);
		assertEquals(5, ((Patient)care.patients.get(care.random.nextInt(20))).b_p_i.length);
		assertEquals(5, ((Patient)care.patients.get(care.random.nextInt(20))).b_p_i_1.length);
		assertEquals(5, ((Patient)care.patients.get(care.random.nextInt(20))).c_p_i.length);
		assertEquals(5, ((Patient)care.patients.get(care.random.nextInt(20))).c_p_i_1.length);
		//Check provider's availability
		assertTrue(care.appointer.appoint(care.random.nextInt(5), care.random.nextInt(20), 2.0)[0] > -1, "Provider is not available, but should have"); 
		//Check that observer has "W" providers in B, E, and C
		assertEquals(5, care.observer.E_p_w_i[care.random.nextInt(20)].length);
		assertEquals(5, care.observer.B_p_w_i[care.random.nextInt(20)].length);
		assertEquals(5, care.observer.C_p_w_i[care.random.nextInt(20)].length);
		
		//assign a value to check that will be preserved
		int pat = care.random.nextInt(20);
		int pro = care.random.nextInt(5);
		int loc = care.random.nextInt(10);
		care.observer.B_p_w_i[pat][pro][loc] = 6553200;
		care.observer.C_p_w_i[pat][pro][loc] = 8200;
		care.observer.E_p_w_i[pat][pro][loc] = 1298;

		//care.observer.B_p_w_i[care.random.nextInt(pat)][pro][loc] = 6553200;

		//2: INCREASE W
		//System.out.println("(TEST) increasing capacity");
		care.change_W_midwaytrhough(10);		
		for (int i=10;i<20;i++) {
			care.schedule.step(care);

		}
		
		//Check the number of providers
		assertEquals(10, care.providers.numObjs);
		//Check patient's E of providers
		assertEquals(10, ((Patient)care.patients.get(care.random.nextInt(10))).e_p_i.length);
		assertEquals(10, ((Patient)care.patients.get(care.random.nextInt(10))).e_p_i_1.length);
		assertEquals(10, ((Patient)care.patients.get(care.random.nextInt(10))).b_p_i.length);
		assertEquals(10, ((Patient)care.patients.get(care.random.nextInt(10))).b_p_i_1.length);
		assertEquals(10, ((Patient)care.patients.get(care.random.nextInt(10))).c_p_i.length);
		assertEquals(10, ((Patient)care.patients.get(care.random.nextInt(10))).c_p_i_1.length);
		assertEquals(10, ((Patient)care.patients.get(care.random.nextInt(10))).c_p_i_counter.length);
		//Check provider's availability
		assertTrue(care.appointer.appoint(care.random.nextInt(5)+5, 0, 3)[0] > -1);
		//Check total capacity was preserved
		currentCapacity = 0;
		for (int p =0; p<care.providers.numObjs; p++) {
			currentCapacity+=((Provider)care.providers.get(p)).A_w;
		}
		assertEquals(care.totalCapacity, currentCapacity, "Unexpected change in currentCapacity happened");
		//Check that observer has "W" providers in B or E or C
		assertEquals(10, care.observer.B_p_w_i[care.random.nextInt(20)].length);
		assertEquals(10, care.observer.C_p_w_i[care.random.nextInt(20)].length);
		assertEquals(10, care.observer.E_p_w_i[care.random.nextInt(20)].length);

		//Check that information in B,E or C was preserved after adding W
		assertEquals(6553200, care.observer.B_p_w_i[pat][pro][loc]);
		assertEquals(8200, care.observer.C_p_w_i[pat][pro][loc]);
		assertEquals(1298, care.observer.E_p_w_i[pat][pro][loc]);
		
		//Check that previous to creation of a W there is no information in observers
		for(int i =0;i<10;i++) {
			pat = care.random.nextInt(20);
			pro = care.random.nextInt(5)+5;
			loc = care.random.nextInt(10);
		assertEquals(-1, care.observer.B_p_w_i[pat][pro][loc], "Failed at patient "+pat+" provider "+pro+" loc "+loc);}
		
		
		//assign a value to check that will be preserved
		pat = care.random.nextInt(20);
		pro = care.random.nextInt(10);
		loc = care.random.nextInt(10)+10;
		care.observer.B_p_w_i[pat][pro][loc] = 6553200;
		care.observer.C_p_w_i[pat][pro][loc] = 8200;
		care.observer.E_p_w_i[pat][pro][loc] = 1298;
		
		//3: DECREASE W
		//System.out.println("(TEST) reducing capacity");
		care.change_W_midwaytrhough(3);
		
		
		for (int i=20;i<30;i++) {
			care.schedule.step(care);
		}

		//Check the number of providers
		assertEquals(3, care.providers.numObjs);
		//Total capacity was preserved
		assertEquals(51, care.totalCapacity, "Total capacity should be 51");
		//Check that total capacity was assigned correctly
		currentCapacity = 0;
		for (int p =0; p<care.providers.numObjs; p++) {
			currentCapacity+=((Provider)care.providers.get(p)).A_w;
		}
		assertEquals(51, currentCapacity, "Total capacity should be 51");
		//Check patient's arrays to providers
		assertEquals(10, ((Patient)care.patients.get(care.random.nextInt(10))).e_p_i.length);
		assertEquals(10, ((Patient)care.patients.get(care.random.nextInt(10))).e_p_i_1.length);
		assertEquals(10, ((Patient)care.patients.get(care.random.nextInt(10))).b_p_i.length);
		assertEquals(10, ((Patient)care.patients.get(care.random.nextInt(10))).b_p_i_1.length);
		assertEquals(10, ((Patient)care.patients.get(care.random.nextInt(10))).c_p_i.length);
		assertEquals(10, ((Patient)care.patients.get(care.random.nextInt(10))).c_p_i_1.length);
		assertEquals(10, ((Patient)care.patients.get(care.random.nextInt(10))).c_p_i_counter.length);

		//Check that observer has "W" providers in B or E or C
		assertEquals(10, care.observer.B_p_w_i[care.random.nextInt(20)].length);
		assertEquals(10, care.observer.C_p_w_i[care.random.nextInt(20)].length);
		assertEquals(10, care.observer.E_p_w_i[care.random.nextInt(20)].length);
		
		
	}
	
	
}


