/*
 * Copyright 1997-2019 Optimatika
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.ojalgo.array.operation;

import java.util.Arrays;

import org.ojalgo.concurrent.DivideAndConquer;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.structure.Access1D;
import org.ojalgo.structure.Structure2D;

public final class MultiplyLeft implements ArrayOperation {

    public interface Generic<N extends Scalar<N>> {

        void invoke(N[] product, Access1D<N> left, int complexity, N[] right, Scalar.Factory<N> scalar);

    }

    public interface Primitive32 {

        void invoke(float[] product, Access1D<?> left, int complexity, float[] right);

    }

    public interface Primitive64 {

        void invoke(double[] product, Access1D<?> left, int complexity, double[] right);

    }

    public static int THRESHOLD = 32;

    static final MultiplyLeft.Primitive64 PRIMITIVE = (product, left, complexity, right) -> {

        Arrays.fill(product, 0.0);

        MultiplyLeft.invoke(product, 0, right.length / complexity, left, complexity, right);
    };

    static final MultiplyLeft.Primitive32 PRIMITIVE32 = (product, left, complexity, right) -> {

        Arrays.fill(product, 0F);

        MultiplyLeft.invoke(product, 0, right.length / complexity, left, complexity, right);
    };

    static final MultiplyLeft.Primitive64 PRIMITIVE_0XN = (product, left, complexity, right) -> {

        final int tmpRowDim = 10;
        final int tmpColDim = right.length / complexity;

        for (int j = 0; j < tmpColDim; j++) {

            double tmp0J = PrimitiveMath.ZERO;
            double tmp1J = PrimitiveMath.ZERO;
            double tmp2J = PrimitiveMath.ZERO;
            double tmp3J = PrimitiveMath.ZERO;
            double tmp4J = PrimitiveMath.ZERO;
            double tmp5J = PrimitiveMath.ZERO;
            double tmp6J = PrimitiveMath.ZERO;
            double tmp7J = PrimitiveMath.ZERO;
            double tmp8J = PrimitiveMath.ZERO;
            double tmp9J = PrimitiveMath.ZERO;

            int tmpIndex = 0;
            for (int c = 0; c < complexity; c++) {
                final double tmpRightCJ = right[c + (j * complexity)];
                tmp0J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp1J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp2J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp3J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp4J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp5J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp6J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp7J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp8J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp9J += left.doubleValue(tmpIndex++) * tmpRightCJ;
            }

            product[tmpIndex = j * tmpRowDim] = tmp0J;
            product[++tmpIndex] = tmp1J;
            product[++tmpIndex] = tmp2J;
            product[++tmpIndex] = tmp3J;
            product[++tmpIndex] = tmp4J;
            product[++tmpIndex] = tmp5J;
            product[++tmpIndex] = tmp6J;
            product[++tmpIndex] = tmp7J;
            product[++tmpIndex] = tmp8J;
            product[++tmpIndex] = tmp9J;
        }
    };

    static final MultiplyLeft.Primitive64 PRIMITIVE_1X1 = (product, left, complexity, right) -> {

        double tmp00 = PrimitiveMath.ZERO;

        for (int c = 0; c < complexity; c++) {
            tmp00 += left.doubleValue(c) * right[c];
        }

        product[0] = tmp00;
    };

    static final MultiplyLeft.Primitive64 PRIMITIVE_1XN = (product, left, complexity, right) -> {

        final int tmpColDim = right.length / complexity;

        for (int j = 0; j < tmpColDim; j++) {

            double tmp0J = PrimitiveMath.ZERO;

            int tmpIndex = 0;
            for (int c = 0; c < complexity; c++) {
                tmp0J += left.doubleValue(tmpIndex++) * right[c + (j * complexity)];
            }

            product[j] = tmp0J;
        }
    };

    static final MultiplyLeft.Primitive64 PRIMITIVE_2X2 = (product, left, complexity, right) -> {

        double tmp00 = PrimitiveMath.ZERO;
        double tmp10 = PrimitiveMath.ZERO;
        double tmp01 = PrimitiveMath.ZERO;
        double tmp11 = PrimitiveMath.ZERO;

        int tmpIndex;
        for (int c = 0; c < complexity; c++) {

            tmpIndex = c * 2;
            final double tmpLeft0 = left.doubleValue(tmpIndex);
            tmpIndex++;
            final double tmpLeft1 = left.doubleValue(tmpIndex);
            tmpIndex = c;
            final double tmpRight0 = right[tmpIndex];
            tmpIndex += complexity;
            final double tmpRight1 = right[tmpIndex];

            tmp00 += tmpLeft0 * tmpRight0;
            tmp10 += tmpLeft1 * tmpRight0;
            tmp01 += tmpLeft0 * tmpRight1;
            tmp11 += tmpLeft1 * tmpRight1;
        }

        product[0] = tmp00;
        product[1] = tmp10;
        product[2] = tmp01;
        product[3] = tmp11;
    };

    static final MultiplyLeft.Primitive64 PRIMITIVE_3X3 = (product, left, complexity, right) -> {

        double tmp00 = PrimitiveMath.ZERO;
        double tmp10 = PrimitiveMath.ZERO;
        double tmp20 = PrimitiveMath.ZERO;
        double tmp01 = PrimitiveMath.ZERO;
        double tmp11 = PrimitiveMath.ZERO;
        double tmp21 = PrimitiveMath.ZERO;
        double tmp02 = PrimitiveMath.ZERO;
        double tmp12 = PrimitiveMath.ZERO;
        double tmp22 = PrimitiveMath.ZERO;

        int tmpIndex;
        for (int c = 0; c < complexity; c++) {

            tmpIndex = c * 3;
            final double tmpLeft0 = left.doubleValue(tmpIndex);
            tmpIndex++;
            final double tmpLeft1 = left.doubleValue(tmpIndex);
            tmpIndex++;
            final double tmpLeft2 = left.doubleValue(tmpIndex);
            tmpIndex = c;
            final double tmpRight0 = right[tmpIndex];
            tmpIndex += complexity;
            final double tmpRight1 = right[tmpIndex];
            tmpIndex += complexity;
            final double tmpRight2 = right[tmpIndex];

            tmp00 += tmpLeft0 * tmpRight0;
            tmp10 += tmpLeft1 * tmpRight0;
            tmp20 += tmpLeft2 * tmpRight0;
            tmp01 += tmpLeft0 * tmpRight1;
            tmp11 += tmpLeft1 * tmpRight1;
            tmp21 += tmpLeft2 * tmpRight1;
            tmp02 += tmpLeft0 * tmpRight2;
            tmp12 += tmpLeft1 * tmpRight2;
            tmp22 += tmpLeft2 * tmpRight2;
        }

        product[0] = tmp00;
        product[1] = tmp10;
        product[2] = tmp20;
        product[3] = tmp01;
        product[4] = tmp11;
        product[5] = tmp21;
        product[6] = tmp02;
        product[7] = tmp12;
        product[8] = tmp22;
    };

    static final MultiplyLeft.Primitive64 PRIMITIVE_4X4 = (product, left, complexity, right) -> {

        double tmp00 = PrimitiveMath.ZERO;
        double tmp10 = PrimitiveMath.ZERO;
        double tmp20 = PrimitiveMath.ZERO;
        double tmp30 = PrimitiveMath.ZERO;
        double tmp01 = PrimitiveMath.ZERO;
        double tmp11 = PrimitiveMath.ZERO;
        double tmp21 = PrimitiveMath.ZERO;
        double tmp31 = PrimitiveMath.ZERO;
        double tmp02 = PrimitiveMath.ZERO;
        double tmp12 = PrimitiveMath.ZERO;
        double tmp22 = PrimitiveMath.ZERO;
        double tmp32 = PrimitiveMath.ZERO;
        double tmp03 = PrimitiveMath.ZERO;
        double tmp13 = PrimitiveMath.ZERO;
        double tmp23 = PrimitiveMath.ZERO;
        double tmp33 = PrimitiveMath.ZERO;

        int tmpIndex;
        for (int c = 0; c < complexity; c++) {

            tmpIndex = c * 4;
            final double tmpLeft0 = left.doubleValue(tmpIndex);
            tmpIndex++;
            final double tmpLeft1 = left.doubleValue(tmpIndex);
            tmpIndex++;
            final double tmpLeft2 = left.doubleValue(tmpIndex);
            tmpIndex++;
            final double tmpLeft3 = left.doubleValue(tmpIndex);
            tmpIndex = c;
            final double tmpRight0 = right[tmpIndex];
            tmpIndex += complexity;
            final double tmpRight1 = right[tmpIndex];
            tmpIndex += complexity;
            final double tmpRight2 = right[tmpIndex];
            tmpIndex += complexity;
            final double tmpRight3 = right[tmpIndex];

            tmp00 += tmpLeft0 * tmpRight0;
            tmp10 += tmpLeft1 * tmpRight0;
            tmp20 += tmpLeft2 * tmpRight0;
            tmp30 += tmpLeft3 * tmpRight0;
            tmp01 += tmpLeft0 * tmpRight1;
            tmp11 += tmpLeft1 * tmpRight1;
            tmp21 += tmpLeft2 * tmpRight1;
            tmp31 += tmpLeft3 * tmpRight1;
            tmp02 += tmpLeft0 * tmpRight2;
            tmp12 += tmpLeft1 * tmpRight2;
            tmp22 += tmpLeft2 * tmpRight2;
            tmp32 += tmpLeft3 * tmpRight2;
            tmp03 += tmpLeft0 * tmpRight3;
            tmp13 += tmpLeft1 * tmpRight3;
            tmp23 += tmpLeft2 * tmpRight3;
            tmp33 += tmpLeft3 * tmpRight3;
        }

        product[0] = tmp00;
        product[1] = tmp10;
        product[2] = tmp20;
        product[3] = tmp30;
        product[4] = tmp01;
        product[5] = tmp11;
        product[6] = tmp21;
        product[7] = tmp31;
        product[8] = tmp02;
        product[9] = tmp12;
        product[10] = tmp22;
        product[11] = tmp32;
        product[12] = tmp03;
        product[13] = tmp13;
        product[14] = tmp23;
        product[15] = tmp33;
    };

    static final MultiplyLeft.Primitive64 PRIMITIVE_5X5 = (product, left, complexity, right) -> {

        double tmp00 = PrimitiveMath.ZERO;
        double tmp10 = PrimitiveMath.ZERO;
        double tmp20 = PrimitiveMath.ZERO;
        double tmp30 = PrimitiveMath.ZERO;
        double tmp40 = PrimitiveMath.ZERO;
        double tmp01 = PrimitiveMath.ZERO;
        double tmp11 = PrimitiveMath.ZERO;
        double tmp21 = PrimitiveMath.ZERO;
        double tmp31 = PrimitiveMath.ZERO;
        double tmp41 = PrimitiveMath.ZERO;
        double tmp02 = PrimitiveMath.ZERO;
        double tmp12 = PrimitiveMath.ZERO;
        double tmp22 = PrimitiveMath.ZERO;
        double tmp32 = PrimitiveMath.ZERO;
        double tmp42 = PrimitiveMath.ZERO;
        double tmp03 = PrimitiveMath.ZERO;
        double tmp13 = PrimitiveMath.ZERO;
        double tmp23 = PrimitiveMath.ZERO;
        double tmp33 = PrimitiveMath.ZERO;
        double tmp43 = PrimitiveMath.ZERO;
        double tmp04 = PrimitiveMath.ZERO;
        double tmp14 = PrimitiveMath.ZERO;
        double tmp24 = PrimitiveMath.ZERO;
        double tmp34 = PrimitiveMath.ZERO;
        double tmp44 = PrimitiveMath.ZERO;

        int tmpIndex;
        for (int c = 0; c < complexity; c++) {

            tmpIndex = c * 5;
            final double tmpLeft0 = left.doubleValue(tmpIndex);
            tmpIndex++;
            final double tmpLeft1 = left.doubleValue(tmpIndex);
            tmpIndex++;
            final double tmpLeft2 = left.doubleValue(tmpIndex);
            tmpIndex++;
            final double tmpLeft3 = left.doubleValue(tmpIndex);
            tmpIndex++;
            final double tmpLeft4 = left.doubleValue(tmpIndex);
            tmpIndex = c;
            final double tmpRight0 = right[tmpIndex];
            tmpIndex += complexity;
            final double tmpRight1 = right[tmpIndex];
            tmpIndex += complexity;
            final double tmpRight2 = right[tmpIndex];
            tmpIndex += complexity;
            final double tmpRight3 = right[tmpIndex];
            tmpIndex += complexity;
            final double tmpRight4 = right[tmpIndex];

            tmp00 += tmpLeft0 * tmpRight0;
            tmp10 += tmpLeft1 * tmpRight0;
            tmp20 += tmpLeft2 * tmpRight0;
            tmp30 += tmpLeft3 * tmpRight0;
            tmp40 += tmpLeft4 * tmpRight0;
            tmp01 += tmpLeft0 * tmpRight1;
            tmp11 += tmpLeft1 * tmpRight1;
            tmp21 += tmpLeft2 * tmpRight1;
            tmp31 += tmpLeft3 * tmpRight1;
            tmp41 += tmpLeft4 * tmpRight1;
            tmp02 += tmpLeft0 * tmpRight2;
            tmp12 += tmpLeft1 * tmpRight2;
            tmp22 += tmpLeft2 * tmpRight2;
            tmp32 += tmpLeft3 * tmpRight2;
            tmp42 += tmpLeft4 * tmpRight2;
            tmp03 += tmpLeft0 * tmpRight3;
            tmp13 += tmpLeft1 * tmpRight3;
            tmp23 += tmpLeft2 * tmpRight3;
            tmp33 += tmpLeft3 * tmpRight3;
            tmp43 += tmpLeft4 * tmpRight3;
            tmp04 += tmpLeft0 * tmpRight4;
            tmp14 += tmpLeft1 * tmpRight4;
            tmp24 += tmpLeft2 * tmpRight4;
            tmp34 += tmpLeft3 * tmpRight4;
            tmp44 += tmpLeft4 * tmpRight4;
        }

        product[0] = tmp00;
        product[1] = tmp10;
        product[2] = tmp20;
        product[3] = tmp30;
        product[4] = tmp40;
        product[5] = tmp01;
        product[6] = tmp11;
        product[7] = tmp21;
        product[8] = tmp31;
        product[9] = tmp41;
        product[10] = tmp02;
        product[11] = tmp12;
        product[12] = tmp22;
        product[13] = tmp32;
        product[14] = tmp42;
        product[15] = tmp03;
        product[16] = tmp13;
        product[17] = tmp23;
        product[18] = tmp33;
        product[19] = tmp43;
        product[20] = tmp04;
        product[21] = tmp14;
        product[22] = tmp24;
        product[23] = tmp34;
        product[24] = tmp44;
    };

    static final MultiplyLeft.Primitive64 PRIMITIVE_6XN = (product, left, complexity, right) -> {

        final int tmpRowDim = 6;
        final int tmpColDim = right.length / complexity;

        for (int j = 0; j < tmpColDim; j++) {

            double tmp0J = PrimitiveMath.ZERO;
            double tmp1J = PrimitiveMath.ZERO;
            double tmp2J = PrimitiveMath.ZERO;
            double tmp3J = PrimitiveMath.ZERO;
            double tmp4J = PrimitiveMath.ZERO;
            double tmp5J = PrimitiveMath.ZERO;

            int tmpIndex = 0;
            for (int c = 0; c < complexity; c++) {
                final double tmpRightCJ = right[c + (j * complexity)];
                tmp0J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp1J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp2J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp3J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp4J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp5J += left.doubleValue(tmpIndex++) * tmpRightCJ;
            }

            product[tmpIndex = j * tmpRowDim] = tmp0J;
            product[++tmpIndex] = tmp1J;
            product[++tmpIndex] = tmp2J;
            product[++tmpIndex] = tmp3J;
            product[++tmpIndex] = tmp4J;
            product[++tmpIndex] = tmp5J;
        }
    };

    static final MultiplyLeft.Primitive64 PRIMITIVE_7XN = (product, left, complexity, right) -> {

        final int tmpRowDim = 7;
        final int tmpColDim = right.length / complexity;

        for (int j = 0; j < tmpColDim; j++) {

            double tmp0J = PrimitiveMath.ZERO;
            double tmp1J = PrimitiveMath.ZERO;
            double tmp2J = PrimitiveMath.ZERO;
            double tmp3J = PrimitiveMath.ZERO;
            double tmp4J = PrimitiveMath.ZERO;
            double tmp5J = PrimitiveMath.ZERO;
            double tmp6J = PrimitiveMath.ZERO;

            int tmpIndex = 0;
            for (int c = 0; c < complexity; c++) {
                final double tmpRightCJ = right[c + (j * complexity)];
                tmp0J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp1J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp2J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp3J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp4J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp5J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp6J += left.doubleValue(tmpIndex++) * tmpRightCJ;
            }

            product[tmpIndex = j * tmpRowDim] = tmp0J;
            product[++tmpIndex] = tmp1J;
            product[++tmpIndex] = tmp2J;
            product[++tmpIndex] = tmp3J;
            product[++tmpIndex] = tmp4J;
            product[++tmpIndex] = tmp5J;
            product[++tmpIndex] = tmp6J;
        }
    };

    static final MultiplyLeft.Primitive64 PRIMITIVE_8XN = (product, left, complexity, right) -> {

        final int tmpRowDim = 8;
        final int tmpColDim = right.length / complexity;

        for (int j = 0; j < tmpColDim; j++) {

            double tmp0J = PrimitiveMath.ZERO;
            double tmp1J = PrimitiveMath.ZERO;
            double tmp2J = PrimitiveMath.ZERO;
            double tmp3J = PrimitiveMath.ZERO;
            double tmp4J = PrimitiveMath.ZERO;
            double tmp5J = PrimitiveMath.ZERO;
            double tmp6J = PrimitiveMath.ZERO;
            double tmp7J = PrimitiveMath.ZERO;

            int tmpIndex = 0;
            for (int c = 0; c < complexity; c++) {
                final double tmpRightCJ = right[c + (j * complexity)];
                tmp0J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp1J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp2J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp3J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp4J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp5J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp6J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp7J += left.doubleValue(tmpIndex++) * tmpRightCJ;
            }

            product[tmpIndex = j * tmpRowDim] = tmp0J;
            product[++tmpIndex] = tmp1J;
            product[++tmpIndex] = tmp2J;
            product[++tmpIndex] = tmp3J;
            product[++tmpIndex] = tmp4J;
            product[++tmpIndex] = tmp5J;
            product[++tmpIndex] = tmp6J;
            product[++tmpIndex] = tmp7J;
        }
    };

    static final MultiplyLeft.Primitive64 PRIMITIVE_9XN = (product, left, complexity, right) -> {

        final int tmpRowDim = 9;
        final int tmpColDim = right.length / complexity;

        for (int j = 0; j < tmpColDim; j++) {

            double tmp0J = PrimitiveMath.ZERO;
            double tmp1J = PrimitiveMath.ZERO;
            double tmp2J = PrimitiveMath.ZERO;
            double tmp3J = PrimitiveMath.ZERO;
            double tmp4J = PrimitiveMath.ZERO;
            double tmp5J = PrimitiveMath.ZERO;
            double tmp6J = PrimitiveMath.ZERO;
            double tmp7J = PrimitiveMath.ZERO;
            double tmp8J = PrimitiveMath.ZERO;

            int tmpIndex = 0;
            for (int c = 0; c < complexity; c++) {
                final double tmpRightCJ = right[c + (j * complexity)];
                tmp0J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp1J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp2J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp3J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp4J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp5J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp6J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp7J += left.doubleValue(tmpIndex++) * tmpRightCJ;
                tmp8J += left.doubleValue(tmpIndex++) * tmpRightCJ;
            }

            product[tmpIndex = j * tmpRowDim] = tmp0J;
            product[++tmpIndex] = tmp1J;
            product[++tmpIndex] = tmp2J;
            product[++tmpIndex] = tmp3J;
            product[++tmpIndex] = tmp4J;
            product[++tmpIndex] = tmp5J;
            product[++tmpIndex] = tmp6J;
            product[++tmpIndex] = tmp7J;
            product[++tmpIndex] = tmp8J;
        }
    };

    static final MultiplyLeft.Primitive64 PRIMITIVE_MT = (product, left, complexity, right) -> {

        Arrays.fill(product, 0.0);

        final DivideAndConquer tmpConquerer = new DivideAndConquer() {

            @Override
            public void conquer(final int first, final int limit) {
                MultiplyLeft.invoke(product, first, limit, left, complexity, right);
            }
        };

        tmpConquerer.invoke(0, right.length / complexity, THRESHOLD);
    };

    public static <N extends Scalar<N>> MultiplyLeft.Generic<N> newGeneric(final long rows, final long columns) {

        if (rows > THRESHOLD) {

            return (product, left, complexity, right, scalar) -> {

                Arrays.fill(product, scalar.zero().get());

                final DivideAndConquer tmpConquerer = new DivideAndConquer() {

                    @Override
                    public void conquer(final int first, final int limit) {
                        MultiplyLeft.invoke(product, first, limit, left, complexity, right, scalar);
                    }
                };

                tmpConquerer.invoke(0, right.length / complexity, THRESHOLD);
            };

        } else {

            return (product, left, complexity, right, scalar) -> {

                Arrays.fill(product, scalar.zero().get());

                MultiplyLeft.invoke(product, 0, right.length / complexity, left, complexity, right, scalar);
            };
        }
    }

    public static MultiplyLeft.Primitive32 newPrimitive32(final long rows, final long columns) {
        return PRIMITIVE32;
    }

    public static MultiplyLeft.Primitive64 newPrimitive64(final long rows, final long columns) {
        if (rows > THRESHOLD) {
            return PRIMITIVE_MT;
        } else if (rows == 10) {
            return PRIMITIVE_0XN;
        } else if (rows == 9) {
            return PRIMITIVE_9XN;
        } else if (rows == 8) {
            return PRIMITIVE_8XN;
        } else if (rows == 7) {
            return PRIMITIVE_7XN;
        } else if (rows == 6) {
            return PRIMITIVE_6XN;
        } else if ((rows == 5) && (columns == 5)) {
            return PRIMITIVE_5X5;
        } else if ((rows == 4) && (columns == 4)) {
            return PRIMITIVE_4X4;
        } else if ((rows == 3) && (columns == 3)) {
            return PRIMITIVE_3X3;
        } else if ((rows == 2) && (columns == 2)) {
            return PRIMITIVE_2X2;
        } else if (rows == 1) {
            return PRIMITIVE_1XN;
        } else {
            return PRIMITIVE;
        }
    }

    static void invoke(final double[] product, final int firstColumn, final int columnLimit, final Access1D<?> left, final int complexity,
            final double[] right) {

        final int structure = ((int) left.count()) / complexity;

        final double[] leftColumn = new double[structure];
        for (int c = 0; c < complexity; c++) {

            final int firstInLeftColumn = MatrixStore.firstInColumn(left, c, 0);
            final int limitOfLeftColumn = MatrixStore.limitOfColumn(left, c, structure);

            for (int i = firstInLeftColumn; i < limitOfLeftColumn; i++) {
                leftColumn[i] = left.doubleValue(Structure2D.index(structure, i, c));
            }

            for (int j = firstColumn; j < columnLimit; j++) {
                AXPY.invoke(product, j * structure, right[c + (j * complexity)], leftColumn, 0, firstInLeftColumn, limitOfLeftColumn);
            }
        }
    }

    static void invoke(final float[] product, final int firstColumn, final int columnLimit, final Access1D<?> left, final int complexity, final float[] right) {

        final int structure = ((int) left.count()) / complexity;

        final float[] leftColumn = new float[structure];
        for (int c = 0; c < complexity; c++) {

            final int firstInLeftColumn = MatrixStore.firstInColumn(left, c, 0);
            final int limitOfLeftColumn = MatrixStore.limitOfColumn(left, c, structure);

            for (int i = firstInLeftColumn; i < limitOfLeftColumn; i++) {
                leftColumn[i] = left.floatValue(Structure2D.index(structure, i, c));
            }

            for (int j = firstColumn; j < columnLimit; j++) {
                AXPY.invoke(product, j * structure, right[c + (j * complexity)], leftColumn, 0, firstInLeftColumn, limitOfLeftColumn);
            }
        }
    }

    static <N extends Scalar<N>> void invoke(final N[] product, final int firstColumn, final int columnLimit, final Access1D<N> left, final int complexity,
            final N[] right, final Scalar.Factory<N> scalar) {

        final int structure = ((int) left.count()) / complexity;

        final N[] leftColumn = scalar.newArrayInstance(structure);
        for (int c = 0; c < complexity; c++) {

            final int firstInLeftColumn = MatrixStore.firstInColumn(left, c, 0);
            final int limitOfLeftColumn = MatrixStore.limitOfColumn(left, c, structure);

            for (int i = firstInLeftColumn; i < limitOfLeftColumn; i++) {
                leftColumn[i] = left.get(Structure2D.index(structure, i, c));
            }

            for (int j = firstColumn; j < columnLimit; j++) {
                AXPY.invoke(product, j * structure, right[c + (j * complexity)], leftColumn, 0, firstInLeftColumn, limitOfLeftColumn);
            }
        }
    }

    @Override
    public int threshold() {
        return THRESHOLD;
    }

}
