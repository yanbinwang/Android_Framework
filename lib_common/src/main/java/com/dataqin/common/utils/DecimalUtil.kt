package com.dataqin.common.utils

import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * author:wyb
 * 计算工具类
 * kotlin中使用其自带的方法:
 * （1）a + b -> a.plus(b)
 * （2）a - b -> a.minus(b)
 * （3）a * b -> a.times(b)
 * （4）a / b -> a.div(b)
 * （5）a % b -> a.mod(b)
 */
//------------------------------------计算工具类------------------------------------
/**
 * 加法运算
 */
fun Double.plus(v: Double) = BigDecimal(this.toString()).add(BigDecimal(v.toString())).toDouble()

/**
 * 减法运算
 */
fun Double.minus(v: Double) = BigDecimal(this.toString()).subtract(BigDecimal(v.toString())).toDouble()

/**
 * 乘法运算
 */
fun Double.times(v: Double) = BigDecimal(this.toString()).multiply(BigDecimal(v.toString())).toDouble()

/**
 * 除法运算-当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入
 */
fun Double.times(v: Double, scale: Int = 10) = BigDecimal(this.toString()).divide(BigDecimal(v.toString()), scale, BigDecimal.ROUND_HALF_UP).toDouble()

/**
 * 小数位四舍五入处理
 */
fun Double.round(scale: Int) = BigDecimal(this.toString()).divide(BigDecimal("1"), scale, BigDecimal.ROUND_HALF_UP).toDouble()

/**
 * 当小数位不超过两位时，补0
 */
fun Double.completion() = DecimalFormat("0.00").format(this) ?: ""

/**
 * 当小数位超过两位时，只显示两位，但只有一位或没有，则不需要补0
 */
fun Double.rounding() = DecimalFormat("0.##").format(this) ?: ""