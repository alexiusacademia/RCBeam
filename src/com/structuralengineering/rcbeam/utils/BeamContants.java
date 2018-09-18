package com.structuralengineering.rcbeam.utils;

public class BeamContants {
    /**
     * Modulus of elasticity of steel.
     * Unit in Mega Pascal (MPa).
     */
    public static double ES = 200000;


    /**
     * Maximum strain in concrete
     */
    public static double MAX_CONCRETE_STRAIN = 0.003;

    /**
     * Concrete compressive strength threshold, before
     * changing Beta1
     */
    public static double COMPRESSIVE_STRENGTH_THRESHOLD = 30;

    public static int COMPRESSION_SOLID_DY_ITERATION = 1000;
}
