package org.eclipse.jdt.legacy.formatter;

/**
 * Interface that defines the behavior for formatter processing options with
 * respect to binary operators and expressions.
 *
 * @author isaki
 */
public interface LegacyBinaryOperatorFormatOption {

	/**
	 * @return insert_space_before_binary_operator
	 */
	boolean insertSpaceBeforeBinaryOperator();

	/**
	 * @return insert_space_after_binary_operator
	 */
	boolean insertSpaceAfterBinaryOperator();

	/**
	 * @return wrap_before_binary_operator
	 */
	boolean wrapBeforeBinaryOperator();

	/**
	 * @return alignment_for_binary_expression
	 */
	int alignmentForBinaryExpression();
}
