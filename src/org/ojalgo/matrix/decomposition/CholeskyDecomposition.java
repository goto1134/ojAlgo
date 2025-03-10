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
package org.ojalgo.matrix.decomposition;

import static org.ojalgo.function.constant.PrimitiveMath.*;

import org.ojalgo.RecoverableCondition;
import org.ojalgo.array.BasicArray;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.store.GenericStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.Primitive64Store;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Quaternion;
import org.ojalgo.scalar.RationalNumber;
import org.ojalgo.structure.Access2D;
import org.ojalgo.structure.Access2D.Collectable;
import org.ojalgo.structure.Structure2D;

abstract class CholeskyDecomposition<N extends Comparable<N>> extends InPlaceDecomposition<N> implements Cholesky<N> {

    static final class Complex extends CholeskyDecomposition<ComplexNumber> {

        Complex() {
            super(GenericStore.COMPLEX);
        }

    }

    static final class Primitive extends CholeskyDecomposition<Double> {

        Primitive() {
            super(Primitive64Store.FACTORY);
        }

    }

    static final class Quat extends CholeskyDecomposition<Quaternion> {

        Quat() {
            super(GenericStore.QUATERNION);
        }

    }

    static final class Rational extends CholeskyDecomposition<RationalNumber> {

        Rational() {
            super(GenericStore.RATIONAL);
        }

    }

    private double myMaxDiag = ONE;
    private double myMinDiag = ZERO;
    private boolean mySPD = false;

    protected CholeskyDecomposition(final DecompositionStore.Factory<N, ? extends DecompositionStore<N>> aFactory) {
        super(aFactory);
    }

    public N calculateDeterminant(final Access2D<?> matrix) {
        this.decompose(this.wrap(matrix));
        return this.getDeterminant();
    }

    public boolean checkAndDecompose(final MatrixStore<N> matrix) {
        return this.compute(matrix, true);
    }

    public int countSignificant(final double threshold) {

        double minimum = Math.sqrt(threshold);

        DecompositionStore<N> internal = this.getInPlace();

        int significant = 0;
        for (int ij = 0, limit = this.getMinDim(); ij < limit; ij++) {
            if (internal.doubleValue(ij, ij) > minimum) {
                significant++;
            }
        }

        return significant;
    }

    public final boolean decompose(final Access2D.Collectable<N, ? super PhysicalStore<N>> aStore) {
        return this.compute(aStore, false);
    }

    public N getDeterminant() {

        final AggregatorFunction<N> tmpAggrFunc = this.aggregator().product2();

        this.getInPlace().visitDiagonal(0, 0, tmpAggrFunc);

        return tmpAggrFunc.get();
    }

    @Override
    public final MatrixStore<N> getInverse(final PhysicalStore<N> preallocated) {

        final DecompositionStore<N> body = this.getInPlace();

        preallocated.substituteForwards(body, false, false, true);
        preallocated.substituteBackwards(body, false, true, true);

        return preallocated.logical().hermitian(false).get();
    }

    public MatrixStore<N> getL() {
        return this.getInPlace().logical().triangular(false, false).get();
    }

    public double getRankThreshold() {
        return TEN * myMaxDiag * this.getDimensionalEpsilon();
    }

    public final MatrixStore<N> getSolution(final Collectable<N, ? super PhysicalStore<N>> rhs) {
        return this.getSolution(rhs, this.preallocate(this.getInPlace(), rhs));
    }

    /**
     * Solves [this][X] = [rhs] by first solving
     *
     * <pre>
     * [L][Y] = [RHS]
     * </pre>
     *
     * and then
     *
     * <pre>
     * [U][X] = [Y]
     * </pre>
     *
     * .
     *
     * @param rhs The right hand side
     * @return [X] The solution will be written to "preallocated" and then returned.
     */
    @Override
    public final MatrixStore<N> getSolution(final Collectable<N, ? super PhysicalStore<N>> rhs, final PhysicalStore<N> preallocated) {

        rhs.supplyTo(preallocated);

        final DecompositionStore<N> body = this.getInPlace();

        preallocated.substituteForwards(body, false, false, false);
        preallocated.substituteBackwards(body, false, true, false);

        return preallocated;
    }

    public final MatrixStore<N> invert(final Access2D<?> original) throws RecoverableCondition {

        this.decompose(this.wrap(original));

        if (this.isSolvable()) {
            return this.getInverse();
        } else {
            throw RecoverableCondition.newMatrixNotInvertible();
        }
    }

    public final MatrixStore<N> invert(final Access2D<?> original, final PhysicalStore<N> preallocated) throws RecoverableCondition {

        this.decompose(this.wrap(original));

        if (this.isSolvable()) {
            return this.getInverse(preallocated);
        } else {
            throw RecoverableCondition.newMatrixNotInvertible();
        }
    }

    public final boolean isFullSize() {
        return true;
    }

    public boolean isSPD() {
        return mySPD;
    }

    public PhysicalStore<N> preallocate(final Structure2D template) {
        final long tmpCountRows = template.countRows();
        return this.allocate(tmpCountRows, tmpCountRows);
    }

    public PhysicalStore<N> preallocate(final Structure2D templateBody, final Structure2D templateRHS) {
        return this.allocate(templateRHS.countRows(), templateRHS.countColumns());
    }

    @Override
    public void reset() {

        super.reset();

        mySPD = false;
    }

    public MatrixStore<N> solve(final Access2D<?> body, final Access2D<?> rhs) throws RecoverableCondition {

        this.decompose(this.wrap(body));

        if (this.isSolvable()) {
            return this.getSolution(this.wrap(rhs));
        } else {
            throw RecoverableCondition.newEquationSystemNotSolvable();
        }
    }

    public MatrixStore<N> solve(final Access2D<?> body, final Access2D<?> rhs, final PhysicalStore<N> preallocated) throws RecoverableCondition {

        this.decompose(this.wrap(body));

        if (this.isSolvable()) {
            return this.getSolution(this.wrap(rhs), preallocated);
        } else {
            throw RecoverableCondition.newEquationSystemNotSolvable();
        }
    }

    @Override
    protected boolean checkSolvability() {
        return mySPD && (myMinDiag > this.getRankThreshold());
    }

    final boolean compute(final Access2D.Collectable<N, ? super PhysicalStore<N>> matrix, final boolean checkHermitian) {

        this.reset();

        final DecompositionStore<N> tmpInPlace = this.setInPlace(matrix);

        final int tmpRowDim = this.getRowDim();
        final int tmpColDim = this.getColDim();
        final int tmpMinDim = Math.min(tmpRowDim, tmpColDim);

        // true if (Hermitian) Positive Definite
        boolean tmpPositiveDefinite = tmpRowDim == tmpColDim;
        myMaxDiag = MACHINE_SMALLEST;
        myMinDiag = MACHINE_LARGEST;

        final BasicArray<N> tmpMultipliers = this.makeArray(tmpRowDim);

        // Check if hermitian, maybe
        if (tmpPositiveDefinite && checkHermitian) {
            tmpPositiveDefinite &= MatrixUtils.isHermitian(tmpInPlace);
        }

        final UnaryFunction<N> tmpSqrtFunc = this.function().sqrt();

        // Main loop - along the diagonal
        for (int ij = 0; tmpPositiveDefinite && (ij < tmpMinDim); ij++) {

            // Do the calculations...
            final double tmpVal = tmpInPlace.doubleValue(ij, ij);
            myMaxDiag = PrimitiveMath.MAX.invoke(myMaxDiag, tmpVal);
            myMinDiag = PrimitiveMath.MIN.invoke(myMinDiag, tmpVal);
            if (tmpVal > PrimitiveMath.ZERO) {

                tmpInPlace.modifyOne(ij, ij, tmpSqrtFunc);

                // Calculate multipliers and copy to local column
                // Current column, below the diagonal
                tmpInPlace.divideAndCopyColumn(ij, ij, tmpMultipliers);

                // Remaining columns, below the diagonal
                tmpInPlace.applyCholesky(ij, tmpMultipliers);

            } else {

                tmpPositiveDefinite = false;
            }
        }

        return this.computed(mySPD = tmpPositiveDefinite);
    }

}
