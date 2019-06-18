package com.lhk.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class MathUtil {

	public static double ceilTo(double value, int point) {
		double base = Math.pow(10, point);

		return Math.ceil(value * base) / base;

	}

	public static float roundTo(float value, int point) {
		double base = Math.pow(10, point);

		return new Float(Math.round(value * base) / base);

	}

	public static float floorTo(float value, int point) {
		double base = Math.pow(10, point);

		return new Float(Math.floor(value * base) / base);

	}

	public static float ceilTo(float value, int point) {
		double base = Math.pow(10, point);

		return new Float(Math.ceil(value * base) / base);

	}

	public static double roundTo(double value, int point) {
		double base = Math.pow(10, point);

		return Math.round(value * base) / base;

	}

	public static double floorTo(double value, int point) {
		double base = Math.pow(10, point);

		return Math.floor(value * base) / base;

	}

    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.add(b2).doubleValue();
    }

    public static double subtract(double v1, double v2, int point) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return roundTo(b1.subtract(b2).doubleValue(), point);
    }

    public static double multiply(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.multiply(b2).doubleValue();
    }

    public static double divide(double v1, double v2, int point) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2, point, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static int compareTo(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.compareTo(b2);
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str.replace(".", "")).matches();
    }

}
