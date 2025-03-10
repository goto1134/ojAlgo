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
package org.ojalgo.array;

import java.math.BigDecimal;
import java.util.List;

import org.ojalgo.ProgrammingError;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.FunctionSet;
import org.ojalgo.function.NullaryFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.VoidFunction;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Quaternion;
import org.ojalgo.scalar.RationalNumber;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.structure.Access1D;
import org.ojalgo.structure.Access2D;
import org.ojalgo.structure.Factory2D;
import org.ojalgo.structure.Mutate2D;
import org.ojalgo.structure.Structure2D;
import org.ojalgo.structure.Transformation2D;

/**
 * Array2D
 *
 * @author apete
 */
public final class Array2D<N extends Comparable<N>> implements Access2D<N>, Access2D.Visitable<N>, Access2D.Aggregatable<N>, Access2D.Sliceable<N>,
        Access2D.Elements, Access2D.IndexOf, Structure2D.ReducibleTo1D<Array1D<N>>, Mutate2D.ModifiableReceiver<N>, Mutate2D.Mixable<N> {

    public static final class Factory<N extends Comparable<N>> implements Factory2D.MayBeSparse<Array2D<N>, Array2D<N>, Array2D<N>> {

        private final BasicArray.Factory<N> myDelegate;

        Factory(final DenseArray.Factory<N> denseArray) {
            super();
            myDelegate = new BasicArray.Factory<>(denseArray);
        }

        public Array2D<N> columns(final Access1D<?>... source) {

            final int tmpColumns = source.length;
            final long tmpRows = source[0].count();

            final BasicArray<N> tmpDelegate = myDelegate.makeToBeFilled(tmpRows, tmpColumns);

            if (tmpDelegate.isPrimitive()) {
                long tmpIndex = 0L;
                for (int j = 0; j < tmpColumns; j++) {
                    final Access1D<?> tmpColumn = source[j];
                    for (long i = 0L; i < tmpRows; i++) {
                        tmpDelegate.set(tmpIndex++, tmpColumn.doubleValue(i));
                    }
                }
            } else {
                long tmpIndex = 0L;
                for (int j = 0; j < tmpColumns; j++) {
                    final Access1D<?> tmpColumn = source[j];
                    for (long i = 0L; i < tmpRows; i++) {
                        tmpDelegate.set(tmpIndex++, tmpColumn.get(i));
                    }
                }
            }

            return tmpDelegate.wrapInArray2D(tmpRows);
        }

        public Array2D<N> columns(final Comparable<?>[]... source) {

            final int tmpColumns = source.length;
            final int tmpRows = source[0].length;

            final BasicArray<N> tmpDelegate = myDelegate.makeToBeFilled(tmpRows, tmpColumns);

            long tmpIndex = 0L;
            for (int j = 0; j < tmpColumns; j++) {
                final Comparable<?>[] tmpColumn = source[j];
                for (int i = 0; i < tmpRows; i++) {
                    tmpDelegate.set(tmpIndex++, tmpColumn[i]);
                }
            }

            return tmpDelegate.wrapInArray2D(tmpRows);
        }

        public Array2D<N> columns(final double[]... source) {

            final int tmpColumns = source.length;
            final int tmpRows = source[0].length;

            final BasicArray<N> tmpDelegate = myDelegate.makeToBeFilled(tmpRows, tmpColumns);

            long tmpIndex = 0L;
            for (int j = 0; j < tmpColumns; j++) {
                final double[] tmpColumn = source[j];
                for (int i = 0; i < tmpRows; i++) {
                    tmpDelegate.set(tmpIndex++, tmpColumn[i]);
                }
            }

            return tmpDelegate.wrapInArray2D(tmpRows);
        }

        public Array2D<N> columns(final List<? extends Comparable<?>>... source) {

            final int tmpColumns = source.length;
            final int tmpRows = source[0].size();

            final BasicArray<N> tmpDelegate = myDelegate.makeToBeFilled(tmpRows, tmpColumns);

            long tmpIndex = 0L;
            for (int j = 0; j < tmpColumns; j++) {
                final List<? extends Comparable<?>> tmpColumn = source[j];
                for (int i = 0; i < tmpRows; i++) {
                    tmpDelegate.set(tmpIndex++, tmpColumn.get(i));
                }
            }

            return tmpDelegate.wrapInArray2D(tmpRows);
        }

        public Array2D<N> copy(final Access2D<?> source) {
            return myDelegate.copy(source).wrapInArray2D(source.countRows());
        }

        @Override
        public FunctionSet<N> function() {
            return myDelegate.function();
        }

        @Override
        public Array2D<N> make(final long rows, final long columns) {
            return this.makeDense(rows, columns);
        }

        public Array2D<N> makeDense(final long rows, final long columns) {
            return myDelegate.makeToBeFilled(rows, columns).wrapInArray2D(rows);
        }

        public Array2D<N> makeFilled(final long rows, final long columns, final NullaryFunction<?> supplier) {

            final BasicArray<N> tmpDelegate = myDelegate.makeToBeFilled(rows, columns);

            long tmpIndex = 0L;
            for (long j = 0L; j < columns; j++) {
                for (long i = 0L; i < rows; i++) {
                    tmpDelegate.set(tmpIndex++, supplier.get());
                }
            }

            return tmpDelegate.wrapInArray2D(rows);
        }

        public Array2D<N> makeSparse(final long rows, final long columns) {
            return myDelegate.makeStructuredZero(rows, columns).wrapInArray2D(rows);
        }

        public Array2D<N> rows(final Access1D<?>... source) {

            final int tmpRows = source.length;
            final long tmpColumns = source[0].count();

            final BasicArray<N> tmpDelegate = myDelegate.makeToBeFilled(tmpRows, tmpColumns);

            if (tmpDelegate.isPrimitive()) {
                for (int i = 0; i < tmpRows; i++) {
                    final Access1D<?> tmpRow = source[i];
                    for (long j = 0L; j < tmpColumns; j++) {
                        tmpDelegate.set(Structure2D.index(tmpRows, i, j), tmpRow.doubleValue(j));
                    }
                }
            } else {
                for (int i = 0; i < tmpRows; i++) {
                    final Access1D<?> tmpRow = source[i];
                    for (long j = 0L; j < tmpColumns; j++) {
                        tmpDelegate.set(Structure2D.index(tmpRows, i, j), tmpRow.get(j));
                    }
                }
            }

            return tmpDelegate.wrapInArray2D(tmpRows);
        }

        public Array2D<N> rows(final Comparable<?>[]... source) {

            final int tmpRows = source.length;
            final int tmpColumns = source[0].length;

            final BasicArray<N> tmpDelegate = myDelegate.makeToBeFilled(tmpRows, tmpColumns);

            for (int i = 0; i < tmpRows; i++) {
                final Comparable<?>[] tmpRow = source[i];
                for (int j = 0; j < tmpColumns; j++) {
                    tmpDelegate.set(Structure2D.index(tmpRows, i, j), tmpRow[j]);
                }
            }

            return tmpDelegate.wrapInArray2D(tmpRows);
        }

        public Array2D<N> rows(final double[]... source) {

            final int tmpRows = source.length;
            final int tmpColumns = source[0].length;

            final BasicArray<N> tmpDelegate = myDelegate.makeToBeFilled(tmpRows, tmpColumns);

            for (int i = 0; i < tmpRows; i++) {
                final double[] tmpRow = source[i];
                for (int j = 0; j < tmpColumns; j++) {
                    tmpDelegate.set(Structure2D.index(tmpRows, i, j), tmpRow[j]);
                }
            }

            return tmpDelegate.wrapInArray2D(tmpRows);
        }

        @SuppressWarnings("unchecked")
        public Array2D<N> rows(final List<? extends Comparable<?>>... source) {

            final int tmpRows = source.length;
            final int tmpColumns = source[0].size();

            final BasicArray<N> tmpDelegate = myDelegate.makeToBeFilled(tmpRows, tmpColumns);

            for (int i = 0; i < tmpRows; i++) {
                final List<? extends Comparable<?>> tmpRow = source[i];
                for (int j = 0; j < tmpColumns; j++) {
                    tmpDelegate.set(Structure2D.index(tmpRows, i, j), tmpRow.get(j));
                }
            }

            return tmpDelegate.wrapInArray2D(tmpRows);
        }

        @Override
        public Scalar.Factory<N> scalar() {
            return myDelegate.scalar();
        }

    }

    public static final Factory<BigDecimal> BIG = new Factory<>(BigArray.FACTORY);
    public static final Factory<ComplexNumber> COMPLEX = new Factory<>(ComplexArray.FACTORY);
    public static final Factory<Double> DIRECT32 = new Factory<>(BufferArray.DIRECT32);
    public static final Factory<Double> DIRECT64 = new Factory<>(BufferArray.DIRECT64);
    public static final Factory<Double> PRIMITIVE32 = new Factory<>(Primitive32Array.FACTORY);
    public static final Factory<Double> PRIMITIVE64 = new Factory<>(Primitive64Array.FACTORY);
    public static final Factory<Quaternion> QUATERNION = new Factory<>(QuaternionArray.FACTORY);
    public static final Factory<RationalNumber> RATIONAL = new Factory<>(RationalArray.FACTORY);

    public static <N extends Comparable<N>> Array2D.Factory<N> factory(final DenseArray.Factory<N> denseArray) {
        return new Array2D.Factory<>(denseArray);
    }

    private final long myColumnsCount;
    private final BasicArray<N> myDelegate;
    private final long myRowsCount;

    Array2D(final BasicArray<N> delegate, final long structure) {

        super();

        myDelegate = delegate;

        myRowsCount = structure;
        myColumnsCount = structure == 0L ? 0L : delegate.count() / structure;
    }

    @Override
    public void add(final long index, final Comparable<?> addend) {
        myDelegate.add(index, addend);
    }

    @Override
    public void add(final long index, final double addend) {
        myDelegate.add(index, addend);
    }

    @Override
    public void add(final long index, final float addend) {
        myDelegate.add(index, addend);
    }

    @Override
    public void add(final long row, final long col, final Comparable<?> addend) {
        myDelegate.add(Structure2D.index(myRowsCount, row, col), addend);
    }

    @Override
    public void add(final long row, final long col, final double addend) {
        myDelegate.add(Structure2D.index(myRowsCount, row, col), addend);
    }

    @Override
    public void add(final long row, final long col, final float addend) {
        myDelegate.add(Structure2D.index(myRowsCount, row, col), addend);
    }

    @Override
    public N aggregateColumn(final long row, final long col, final Aggregator aggregator) {
        AggregatorFunction<N> visitor = aggregator.getFunction(myDelegate.factory().aggregator());
        this.visitColumn(row, col, visitor);
        return visitor.get();
    }

    @Override
    public N aggregateDiagonal(final long row, final long col, final Aggregator aggregator) {
        AggregatorFunction<N> visitor = aggregator.getFunction(myDelegate.factory().aggregator());
        this.visitDiagonal(row, col, visitor);
        return visitor.get();
    }

    @Override
    public N aggregateRange(final long first, final long limit, final Aggregator aggregator) {
        AggregatorFunction<N> visitor = aggregator.getFunction(myDelegate.factory().aggregator());
        this.visitRange(first, limit, visitor);
        return visitor.get();
    }

    @Override
    public N aggregateRow(final long row, final long col, final Aggregator aggregator) {
        AggregatorFunction<N> visitor = aggregator.getFunction(myDelegate.factory().aggregator());
        this.visitRow(row, col, visitor);
        return visitor.get();
    }

    /**
     * Flattens this two dimensional array to a one dimensional array. The (internal/actual) array is not
     * copied, it is just accessed through a different adaptor.
     *
     * @deprecated v39 Not needed
     */
    @Deprecated
    public Array1D<N> asArray1D() {
        return myDelegate.wrapInArray1D();
    }

    public void clear() {
        myDelegate.reset();
    }

    @Override
    public long count() {
        return myDelegate.count();
    }

    @Override
    public long countColumns() {
        return myColumnsCount;
    }

    @Override
    public long countRows() {
        return myRowsCount;
    }

    @Override
    public double doubleValue(final long index) {
        return myDelegate.doubleValue(index);
    }

    @Override
    public double doubleValue(final long row, final long col) {
        return myDelegate.doubleValue(Structure2D.index(myRowsCount, row, col));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Array2D) {
            final Array2D<N> tmpObj = (Array2D<N>) obj;
            return (myRowsCount == tmpObj.countRows()) && (myColumnsCount == tmpObj.countColumns()) && myDelegate.equals(tmpObj.getDelegate());
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public void exchangeColumns(final long colA, final long colB) {
        myDelegate.exchange(colA * myRowsCount, colB * myRowsCount, 1L, myRowsCount);
    }

    @Override
    public void exchangeRows(final long rowA, final long rowB) {
        myDelegate.exchange(rowA, rowB, myRowsCount, myColumnsCount);
    }

    @Override
    public void fillAll(final N value) {
        myDelegate.fill(0L, this.count(), 1L, value);
    }

    @Override
    public void fillAll(final NullaryFunction<?> supplier) {
        myDelegate.fill(0L, this.count(), 1L, supplier);
    }

    @Override
    public void fillColumn(final long row, final long col, final Access1D<N> values) {

        final long offset = Structure2D.index(myRowsCount, row, col);
        final long limit = Math.min(this.countRows() - row, values.count());

        if (myDelegate.isPrimitive()) {
            for (long i = 0L; i < limit; i++) {
                this.set(offset + i, values.doubleValue(i));
            }
        } else {
            for (long i = 0L; i < limit; i++) {
                this.fillOne(offset + i, values.get(i));
            }
        }
    }

    @Override
    public void fillColumn(final long row, final long col, final N value) {
        myDelegate.fill(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, myRowsCount, col), 1L, value);
    }

    @Override
    public void fillColumn(final long row, final long col, final NullaryFunction<?> supplier) {
        myDelegate.fill(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, myRowsCount, col), 1L, supplier);
    }

    @Override
    public void fillDiagonal(final long row, final long col, final N value) {
        final long tmpCount = Math.min(myRowsCount - row, myColumnsCount - col);
        myDelegate.fill(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row + tmpCount, col + tmpCount), 1L + myRowsCount, value);
    }

    @Override
    public void fillDiagonal(final long row, final long col, final NullaryFunction<?> supplier) {
        final long tmpCount = Math.min(myRowsCount - row, myColumnsCount - col);
        myDelegate.fill(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row + tmpCount, col + tmpCount), 1L + myRowsCount, supplier);
    }

    @Override
    public void fillOne(final long index, final Access1D<?> values, final long valueIndex) {
        myDelegate.fillOne(index, values, valueIndex);
    }

    @Override
    public void fillOne(final long row, final long col, final Access1D<?> values, final long valueIndex) {
        myDelegate.fillOne(Structure2D.index(myRowsCount, row, col), values, valueIndex);
    }

    @Override
    public void fillOne(final long row, final long col, final N value) {
        myDelegate.fillOne(Structure2D.index(myRowsCount, row, col), value);
    }

    @Override
    public void fillOne(final long row, final long col, final NullaryFunction<?> supplier) {
        myDelegate.fillOne(Structure2D.index(myRowsCount, row, col), supplier);
    }

    @Override
    public void fillOne(final long index, final N value) {
        myDelegate.fillOne(index, value);
    }

    @Override
    public void fillOne(final long index, final NullaryFunction<?> supplier) {
        myDelegate.fillOne(index, supplier);
    }

    @Override
    public void fillRange(final long first, final long limit, final N value) {
        myDelegate.fill(first, limit, 1L, value);
    }

    @Override
    public void fillRange(final long first, final long limit, final NullaryFunction<?> supplier) {
        myDelegate.fill(first, limit, 1L, supplier);
    }

    @Override
    public void fillRow(final long row, final long col, final Access1D<N> values) {

        final long offset = Structure2D.index(myRowsCount, row, col);
        final long limit = Math.min(this.countColumns() - col, values.count());

        if (myDelegate.isPrimitive()) {
            for (long i = 0L; i < limit; i++) {
                this.set(offset + (i * myRowsCount), values.doubleValue(i));
            }
        } else {
            for (long i = 0L; i < limit; i++) {
                this.fillOne(offset + (i * myRowsCount), values.get(i));
            }
        }
    }

    @Override
    public void fillRow(final long row, final long col, final N value) {
        myDelegate.fill(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row, myColumnsCount), myRowsCount, value);
    }

    @Override
    public void fillRow(final long row, final long col, final NullaryFunction<?> supplier) {
        myDelegate.fill(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row, myColumnsCount), myRowsCount, supplier);
    }

    @Override
    public N get(final long index) {
        return myDelegate.get(index);
    }

    @Override
    public N get(final long row, final long col) {
        return myDelegate.get(Structure2D.index(myRowsCount, row, col));
    }

    @Override
    public int hashCode() {
        return (int) (myRowsCount * myColumnsCount * myDelegate.hashCode());
    }

    @Override
    public long indexOfLargest() {
        return myDelegate.indexOfLargest();
    }

    /**
     * @return The row-index of the largest absolute value in a column, starting at the specified row.
     */
    @Override
    public long indexOfLargestInColumn(final long row, final long col) {
        return myDelegate.indexOfLargest(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, myRowsCount, col), 1L) % myRowsCount;
    }

    @Override
    public long indexOfLargestInRange(final long first, final long limit) {
        return myDelegate.indexOfLargestInRange(first, limit);
    }

    /**
     * @return The column-index of the largest absolute value in a row, starting at the specified column.
     */
    @Override
    public long indexOfLargestInRow(final long row, final long col) {
        return myDelegate.indexOfLargest(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row, myColumnsCount), myRowsCount)
                / myRowsCount;
    }

    @Override
    public long indexOfLargestOnDiagonal(final long first) {

        final long tmpMinCount = Math.min(myRowsCount, myColumnsCount);

        final long tmpFirst = Structure2D.index(myRowsCount, first, first);
        final long tmpLimit = Structure2D.index(myRowsCount, tmpMinCount, tmpMinCount);
        final long tmpStep = 1L + myRowsCount;

        return myDelegate.indexOfLargest(tmpFirst, tmpLimit, tmpStep) / myRowsCount;
    }

    @Override
    public boolean isAbsolute(final long index) {
        return myDelegate.isAbsolute(index);
    }

    /**
     * @see Scalar#isAbsolute()
     */
    @Override
    public boolean isAbsolute(final long row, final long col) {
        return myDelegate.isAbsolute(Structure2D.index(myRowsCount, row, col));
    }

    @Override
    public boolean isAllSmall(final double comparedTo) {
        return myDelegate.isSmall(0L, this.count(), 1L, comparedTo);
    }

    @Override
    public boolean isColumnSmall(final long row, final long col, final double comparedTo) {
        return myDelegate.isSmall(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, myRowsCount, col), 1L, comparedTo);
    }

    @Override
    public boolean isRowSmall(final long row, final long col, final double comparedTo) {
        return myDelegate.isSmall(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row, myColumnsCount), myRowsCount, comparedTo);
    }

    @Override
    public boolean isSmall(final long index, final double comparedTo) {
        return myDelegate.isSmall(index, comparedTo);
    }

    @Override
    public boolean isSmall(final long row, final long col, final double comparedTo) {
        return myDelegate.isSmall(Structure2D.index(myRowsCount, row, col), comparedTo);
    }

    @Override
    public double mix(final long row, final long col, final BinaryFunction<N> mixer, final double addend) {
        ProgrammingError.throwIfNull(mixer);
        synchronized (myDelegate) {
            final double oldValue = this.doubleValue(row, col);
            final double newValue = mixer.invoke(oldValue, addend);
            this.set(row, col, newValue);
            return newValue;
        }
    }

    @Override
    public N mix(final long row, final long col, final BinaryFunction<N> mixer, final N addend) {
        ProgrammingError.throwIfNull(mixer);
        synchronized (myDelegate) {
            final N oldValue = this.get(row, col);
            final N newValue = mixer.invoke(oldValue, addend);
            this.set(row, col, newValue);
            return newValue;
        }
    }

    @Override
    public void modifyAll(final UnaryFunction<N> modifier) {
        myDelegate.modify(0L, this.count(), 1L, modifier);
    }

    @Override
    public void modifyAny(final Transformation2D<N> modifier) {
        modifier.transform(this);
    }

    @Override
    public void modifyColumn(final long row, final long col, final UnaryFunction<N> modifier) {
        myDelegate.modify(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, myRowsCount, col), 1L, modifier);
    }

    @Override
    public void modifyDiagonal(final long row, final long col, final UnaryFunction<N> modifier) {
        final long tmpCount = Math.min(myRowsCount - row, myColumnsCount - col);
        myDelegate.modify(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row + tmpCount, col + tmpCount), 1L + myRowsCount, modifier);
    }

    @Override
    public void modifyMatching(final Access1D<N> left, final BinaryFunction<N> function) {
        myDelegate.modify(0L, this.count(), 1L, left, function);
    }

    @Override
    public void modifyMatching(final BinaryFunction<N> function, final Access1D<N> right) {
        myDelegate.modify(0L, this.count(), 1L, function, right);
    }

    @Override
    public void modifyOne(final long row, final long col, final UnaryFunction<N> modifier) {
        myDelegate.modifyOne(Structure2D.index(myRowsCount, row, col), modifier);
    }

    @Override
    public void modifyOne(final long index, final UnaryFunction<N> modifier) {
        myDelegate.modifyOne(index, modifier);
    }

    @Override
    public void modifyRange(final long first, final long limit, final UnaryFunction<N> modifier) {
        myDelegate.modify(first, limit, 1L, modifier);
    }

    @Override
    public void modifyRow(final long row, final long col, final UnaryFunction<N> modifier) {
        myDelegate.modify(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row, myColumnsCount), myRowsCount, modifier);
    }

    @Override
    public Array1D<N> reduceColumns(final Aggregator aggregator) {
        Array1D<N> retVal = myDelegate.factory().makeZero(myColumnsCount).wrapInArray1D();
        this.reduceColumns(aggregator, retVal);
        return retVal;
    }

    @Override
    public Array1D<N> reduceRows(final Aggregator aggregator) {
        Array1D<N> retVal = myDelegate.factory().makeZero(myRowsCount).wrapInArray1D();
        this.reduceRows(aggregator, retVal);
        return retVal;
    }

    @Override
    public void set(final long index, final Comparable<?> value) {
        myDelegate.set(index, value);
    }

    @Override
    public void set(final long index, final double value) {
        myDelegate.set(index, value);
    }

    @Override
    public void set(final long index, final float value) {
        myDelegate.set(index, value);
    }

    @Override
    public void set(final long row, final long col, final Comparable<?> value) {
        myDelegate.set(Structure2D.index(myRowsCount, row, col), value);
    }

    @Override
    public void set(final long row, final long col, final double value) {
        myDelegate.set(Structure2D.index(myRowsCount, row, col), value);
    }

    @Override
    public void set(final long row, final long col, final float value) {
        myDelegate.set(Structure2D.index(myRowsCount, row, col), value);
    }

    @Override
    public Array1D<N> sliceColumn(final long col) {
        return this.sliceColumn(0L, col);
    }

    @Override
    public Array1D<N> sliceColumn(final long row, final long col) {
        return new Array1D<>(myDelegate, Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, myRowsCount, col), 1L);
    }

    @Override
    public Array1D<N> sliceDiagonal(final long row, final long col) {
        final long tmpCount = Math.min(myRowsCount - row, myColumnsCount - col);
        return new Array1D<>(myDelegate, Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row + tmpCount, col + tmpCount),
                1L + myRowsCount);
    }

    @Override
    public Array1D<N> sliceRange(final long first, final long limit) {
        return myDelegate.wrapInArray1D().sliceRange(first, limit);
    }

    @Override
    public Array1D<N> sliceRow(final long row) {
        return this.sliceRow(row, 0L);
    }

    @Override
    public Array1D<N> sliceRow(final long row, final long col) {
        return new Array1D<>(myDelegate, Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row, myColumnsCount), myRowsCount);
    }

    @Override
    public String toString() {
        return Access2D.toString(this);
    }

    @Override
    public void visitAll(final VoidFunction<N> visitor) {
        myDelegate.visit(0L, this.count(), 1L, visitor);
    }

    @Override
    public void visitColumn(final long row, final long col, final VoidFunction<N> visitor) {
        myDelegate.visit(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, myRowsCount, col), 1L, visitor);
    }

    @Override
    public void visitDiagonal(final long row, final long col, final VoidFunction<N> visitor) {
        final long tmpCount = Math.min(myRowsCount - row, myColumnsCount - col);
        myDelegate.visit(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row + tmpCount, col + tmpCount), 1L + myRowsCount, visitor);
    }

    @Override
    public void visitOne(final long row, final long col, final VoidFunction<N> visitor) {
        myDelegate.visitOne(Structure2D.index(myRowsCount, row, col), visitor);
    }

    @Override
    public void visitOne(final long index, final VoidFunction<N> visitor) {
        myDelegate.visitOne(index, visitor);
    }

    @Override
    public void visitRange(final long first, final long limit, final VoidFunction<N> visitor) {
        myDelegate.visit(first, limit, 1L, visitor);
    }

    @Override
    public void visitRow(final long row, final long col, final VoidFunction<N> visitor) {
        myDelegate.visit(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row, myColumnsCount), myRowsCount, visitor);
    }

    BasicArray<N> getDelegate() {
        return myDelegate;
    }

}
