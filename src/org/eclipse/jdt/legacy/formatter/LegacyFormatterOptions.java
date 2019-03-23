package org.eclipse.jdt.legacy.formatter;

import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatterOptions;

/**
 * This class exists to act as an adapter so that legacy formatters can operate
 * even as Eclipse changes the code they depend on.
 *
 * @author isaki
 */
@SuppressWarnings("restriction")
public final class LegacyFormatterOptions {

	private final DefaultCodeFormatterOptions delegate;

	/**
	 * Constructor.
	 *
	 * @param delegate
	 */
	public LegacyFormatterOptions(final DefaultCodeFormatterOptions delegate) {
		super();
		this.delegate = delegate;
	}

	/**
	 * @return insert_new_line_in_empty_annotation_declaration
	 */
	public boolean insertNewLineInEmptyAnnotationDeclaration() {
		return shouldInsertNewLineIfEmpty(this.delegate.keep_annotation_declaration_on_one_line);
	}

	/**
	 * @return insert_new_line_in_empty_enum_declaration
	 */
	public boolean insertNewLineInEmptyEnumDeclaration() {
		return shouldInsertNewLineIfEmpty(this.delegate.keep_enum_declaration_on_one_line);
	}

	/**
	 * @return insert_new_line_in_empty_enum_constant
	 */
	public boolean insertNewLineInEmptyEnumConstant() {
		return shouldInsertNewLineIfEmpty(this.delegate.keep_enum_constant_declaration_on_one_line);
	}

	/**
	 * @return insert_new_line_in_empty_anonymous_type_declaration
	 */
	public boolean insertNewLineInEmptyAnonTypeDeclaration() {
		return shouldInsertNewLineIfEmpty(this.delegate.keep_anonymous_type_declaration_on_one_line);
	}

	/**
	 * @return insert_new_line_in_empty_method_body
	 */
	public boolean insertNewLineInEmptyMethodBody() {
		return shouldInsertNewLineIfEmpty(this.delegate.keep_method_body_on_one_line);
	}

	/**
	 * @return insert_new_line_in_empty_type_declaration
	 */
	public boolean insertNewLineInEmptyTypeDeclaration() {
		return shouldInsertNewLineIfEmpty(this.delegate.keep_type_declaration_on_one_line);
	}

	/**
	 * In the modern formatter, this has been broken into two settings; if
	 * either has been toggled, then this should be false.
	 *
	 * @return insert_new_line_in_empty_block
	 */
	public boolean insertNewLineInEmptyBlock() {
		return shouldInsertNewLineIfEmpty(this.delegate.keep_code_block_on_one_line) && shouldInsertNewLineIfEmpty(this.delegate.keep_lambda_body_block_on_one_line);
	}

	/**
	 * This is derived from a multitude of settings. If even one is true, then
	 * we are true.
	 *
	 * @return insert_space_before_binary_operator
	 */
	public boolean insertSpaceBeforeBinaryOperator() {
		return this.delegate.insert_space_before_multiplicative_operator
			|| this.delegate.insert_space_before_additive_operator
			|| this.delegate.insert_space_before_string_concatenation
			|| this.delegate.insert_space_before_shift_operator
			|| this.delegate.insert_space_before_relational_operator
			|| this.delegate.insert_space_before_bitwise_operator
			|| this.delegate.insert_space_before_logical_operator;
	}

	/**
	 * This is derived from a multitude of settings. If even one is true, then
	 * we are true.
	 *
	 * @return insert_space_after_binary_operator
	 */
	public boolean insertSpaceAfterBinaryOperator() {
		return this.delegate.insert_space_after_multiplicative_operator
			|| this.delegate.insert_space_after_additive_operator
			|| this.delegate.insert_space_after_string_concatenation
			|| this.delegate.insert_space_after_shift_operator
			|| this.delegate.insert_space_after_relational_operator
			|| this.delegate.insert_space_after_bitwise_operator
			|| this.delegate.insert_space_after_logical_operator;
	}

	/**
	 * This is derived from a multitude of settings. If even one is true, then
	 * we are true.
	 *
	 * @return wrap_before_binary_operator
	 */
	public boolean wrapBeforeBinaryOperator() {
		return this.delegate.wrap_before_multiplicative_operator
			|| this.delegate.wrap_before_additive_operator
			|| this.delegate.wrap_before_string_concatenation
			|| this.delegate.wrap_before_shift_operator
			|| this.delegate.wrap_before_relational_operator
			|| this.delegate.wrap_before_bitwise_operator
			|| this.delegate.wrap_before_logical_operator;
	}

	/**
	 * This is derived from a multitude of settings; it returns the bit-wise OR
	 * of the component values that this option was turned into.
	 *
	 * @return alignment_for_binary_expression
	 */
	public int alignmentForBinaryExpression() {
		return this.delegate.alignment_for_multiplicative_operator
			| this.delegate.alignment_for_additive_operator
			| this.delegate.alignment_for_string_concatenation
			| this.delegate.alignment_for_shift_operator
			| this.delegate.alignment_for_relational_operator
			| this.delegate.alignment_for_bitwise_operator
			| this.delegate.alignment_for_logical_operator;
	}

	/**
	 * Checks the setting against the expected values; this returns a boolean in
	 * line with the modern formatter.
	 *
	 * @param oneLineSetting
	 * @return
	 */
	private static boolean shouldInsertNewLineIfEmpty(final String oneLineSetting) {
		// If not set in the legacy, the default would be false.
		return (oneLineSetting == null) ? false : !DefaultCodeFormatterConstants.ONE_LINE_IF_EMPTY.equals(oneLineSetting);
	}
}
