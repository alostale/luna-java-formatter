package org.eclipse.jdt.legacy.formatter;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatterOptions;

/**
 * This class exists to act as an adapter so that legacy formatters can operate
 * even as Eclipse changes the code they depend on. Decisions on behavior are
 * taken from other classes in the modern code formatter.
 *
 * @author isaki
 * @see org.eclipse.jdt.internal.formatter.linewrap.WrapPreparator
 */
@SuppressWarnings("restriction")
public final class LegacyFormatterOptions {

	private static final int NEON_CACHE_SIZE = 20;

	// Backing store for options.
	private final DefaultCodeFormatterOptions delegate;

	// Create views on the backing delegate. We use these immutable views to
	// avoid having to allocate a new object every time the factory method is
	// invoked while still respecting any dynamic changes that may occur in the
	// backing options class.
	private final LegacyBinaryOperatorFormatOption additive;
	private final LegacyBinaryOperatorFormatOption concat;
	private final LegacyBinaryOperatorFormatOption shift;
	private final LegacyBinaryOperatorFormatOption relational;
	private final LegacyBinaryOperatorFormatOption bitwise;
	private final LegacyBinaryOperatorFormatOption logical;
	private final LegacyBinaryOperatorFormatOption multi;
	private final LegacyBinaryOperatorFormatOption composite;

	// We use this for neon because switch is not an option; for luna we rely on
	// the compiler-optimized integer driven switch rather than a hash lookup.
	private final Map<InfixExpression.Operator, LegacyBinaryOperatorFormatOption> neonCache;

	/**
	 * Constructor.
	 *
	 * @param delegate
	 */
	public LegacyFormatterOptions(final DefaultCodeFormatterOptions delegate) {
		super();
		this.delegate = delegate;

		// Standard operators.
		this.additive = new AdditiveBinaryOption();
		this.shift = new ShiftBinaryOption();
		this.relational = new RelationalBinaryOption();
		this.bitwise = new BitwiseBinaryOption();
		this.logical = new LogicalFormatOption();
		this.multi = new MultiplicativeBinaryOption();

		// Special cases that know when they are in use.
		this.concat = new ConcatBinaryOption();
		this.composite = new CompositeBinaryOption();

		final Map<InfixExpression.Operator, LegacyBinaryOperatorFormatOption> tmp = new HashMap<>(NEON_CACHE_SIZE);

		// -- The following are listed in InfixExpression.Operator --
		// TIMES,
		// DIVIDE,
		// REMAINDER,
		// PLUS,
		// MINUS,
		// LEFT_SHIFT,
		// RIGHT_SHIFT_SIGNED,
		// RIGHT_SHIFT_UNSIGNED,
		// LESS,
		// GREATER,
		// LESS_EQUALS,
		// GREATER_EQUALS,
		// EQUALS,
		// NOT_EQUALS,
		// XOR,
		// OR,
		// AND,
		// CONDITIONAL_OR,
		// CONDITIONAL_AND

		// 1,2,3
		tmp.put(InfixExpression.Operator.TIMES, this.multi);
		tmp.put(InfixExpression.Operator.DIVIDE, this.multi);
		tmp.put(InfixExpression.Operator.REMAINDER, this.multi);

		// 4,5
		tmp.put(InfixExpression.Operator.PLUS, this.additive);
		tmp.put(InfixExpression.Operator.MINUS, this.additive);

		// 6,7,8
		tmp.put(InfixExpression.Operator.LEFT_SHIFT, this.shift);
		tmp.put(InfixExpression.Operator.RIGHT_SHIFT_SIGNED, this.shift);
		tmp.put(InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED, this.shift);

		// 9,10,11,12,14,15
		tmp.put(InfixExpression.Operator.LESS, this.relational);
		tmp.put(InfixExpression.Operator.LESS_EQUALS, this.relational);
		tmp.put(InfixExpression.Operator.GREATER, this.relational);
		tmp.put(InfixExpression.Operator.GREATER_EQUALS, this.relational);
		tmp.put(InfixExpression.Operator.EQUALS, this.relational);
		tmp.put(InfixExpression.Operator.NOT_EQUALS, this.relational);

		// 16,17,18
		tmp.put(InfixExpression.Operator.XOR, this.bitwise);
		tmp.put(InfixExpression.Operator.OR, this.bitwise);
		tmp.put(InfixExpression.Operator.AND, this.bitwise);

		// 19,20
		tmp.put(InfixExpression.Operator.CONDITIONAL_OR, this.logical);
		tmp.put(InfixExpression.Operator.CONDITIONAL_AND, this.logical);

		this.neonCache = tmp;
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
	 * This returns the option implementation to use for the given operator.
	 *
	 * @param operator
	 * @return
	 * @see OperatorIds
	 */
	public LegacyBinaryOperatorFormatOption getFormatOptionForBinaryOperator(final int operator) {
		final LegacyBinaryOperatorFormatOption ret;
		switch (operator) {
			case OperatorIds.AND_AND:
			case OperatorIds.OR_OR:
				ret = this.logical;
				break;
			case OperatorIds.AND:
			case OperatorIds.OR:
			case OperatorIds.XOR:
				ret = this.bitwise;
				break;
			case OperatorIds.LESS:
			case OperatorIds.GREATER:
			case OperatorIds.LESS_EQUAL:
			case OperatorIds.GREATER_EQUAL:
			case OperatorIds.EQUAL_EQUAL:
			case OperatorIds.NOT_EQUAL:
				ret = this.relational;
				break;
			case OperatorIds.MULTIPLY:
			case OperatorIds.DIVIDE:
			case OperatorIds.REMAINDER:
				ret = this.multi;
				break;
			case OperatorIds.LEFT_SHIFT:
			case OperatorIds.RIGHT_SHIFT:
			case OperatorIds.UNSIGNED_RIGHT_SHIFT:
				ret = this.shift;
				break;
			case OperatorIds.MINUS:
			case OperatorIds.PLUS:
				ret = this.additive;
				break;
			default:
				ret = this.composite;
				break;
		}

		return ret;
	}

	/**
	 * The existing {@link InfixExpression.Operator} and {@link OperatorIds}
	 * don't have mention of string concatenation but it is supported and now
	 * has its own options.
	 *
	 * @return
	 */
	public LegacyBinaryOperatorFormatOption getFormatOptionForStringConcat() {
		return this.concat;
	}

	/**
	 * This uses an almalgamation of all options. This method exists to address
	 * a corner case in neon.
	 *
	 * @return
	 */
	public LegacyBinaryOperatorFormatOption getFormatOptionForBinaryComposite() {
		return this.composite;
	}

	/**
	 * This is used by Neon to convert an operator object into an appropriate
	 * settings object.
	 *
	 * @param operator
	 * @return
	 */
	public LegacyBinaryOperatorFormatOption getFormatOptionForBinaryOperator(final InfixExpression.Operator operator) {

		LegacyBinaryOperatorFormatOption ret = this.neonCache.get(operator);
		if (ret == null) {
			ret = this.composite;
		}

		return ret;
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

	/*
	 * Helper classes.
	 */

	/**
	 * This class is for numeric addition/subtraction. Given Neon/Luna
	 * shortcomings, this is only used for subtraction.
	 *
	 * @author isaki
	 */
	private final class AdditiveBinaryOption implements LegacyBinaryOperatorFormatOption {

		AdditiveBinaryOption() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean insertSpaceBeforeBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.insert_space_before_additive_operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean insertSpaceAfterBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.insert_space_after_additive_operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean wrapBeforeBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.wrap_before_additive_operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int alignmentForBinaryExpression() {
			return LegacyFormatterOptions.this.delegate.alignment_for_additive_operator;
		}

	}

	/**
	 * This class handles string concatenation.
	 *
	 * @author isaki
	 */
	private final class ConcatBinaryOption implements LegacyBinaryOperatorFormatOption {

		ConcatBinaryOption() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean insertSpaceBeforeBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.insert_space_before_string_concatenation;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean insertSpaceAfterBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.insert_space_after_string_concatenation;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean wrapBeforeBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.wrap_before_string_concatenation;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int alignmentForBinaryExpression() {
			return LegacyFormatterOptions.this.delegate.alignment_for_string_concatenation;
		}

	}

	/**
	 * This class is for binary shifting.
	 *
	 * @author isaki
	 */
	private final class ShiftBinaryOption implements LegacyBinaryOperatorFormatOption {

		ShiftBinaryOption() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean insertSpaceBeforeBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.insert_space_before_shift_operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean insertSpaceAfterBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.insert_space_after_shift_operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean wrapBeforeBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.wrap_before_shift_operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int alignmentForBinaryExpression() {
			return LegacyFormatterOptions.this.delegate.alignment_for_shift_operator;
		}

	}

	/**
	 * This class is for binary relations.
	 *
	 * @author isaki
	 */
	private final class RelationalBinaryOption implements LegacyBinaryOperatorFormatOption {

		RelationalBinaryOption() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean insertSpaceBeforeBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.insert_space_before_relational_operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean insertSpaceAfterBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.insert_space_after_relational_operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean wrapBeforeBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.wrap_before_relational_operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int alignmentForBinaryExpression() {
			return LegacyFormatterOptions.this.delegate.alignment_for_relational_operator;
		}

	}

	/**
	 * This class is for bitwise operations.
	 *
	 * @author isaki
	 */
	private final class BitwiseBinaryOption implements LegacyBinaryOperatorFormatOption {

		BitwiseBinaryOption() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean insertSpaceBeforeBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.insert_space_before_bitwise_operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean insertSpaceAfterBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.insert_space_after_bitwise_operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean wrapBeforeBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.wrap_before_bitwise_operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int alignmentForBinaryExpression() {
			return LegacyFormatterOptions.this.delegate.alignment_for_bitwise_operator;
		}

	}

	/**
	 * This class is for conditional logic formatting.
	 *
	 * @author isaki
	 */
	private final class LogicalFormatOption implements LegacyBinaryOperatorFormatOption {

		LogicalFormatOption() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean insertSpaceBeforeBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.insert_space_before_logical_operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean insertSpaceAfterBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.insert_space_after_logical_operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean wrapBeforeBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.wrap_before_logical_operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int alignmentForBinaryExpression() {
			return LegacyFormatterOptions.this.delegate.alignment_for_logical_operator;
		}

	}

	/**
	 * This class is for multiplicative operations.
	 *
	 * @author isaki
	 */
	private final class MultiplicativeBinaryOption implements LegacyBinaryOperatorFormatOption {

		MultiplicativeBinaryOption() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean insertSpaceBeforeBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.insert_space_before_multiplicative_operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean insertSpaceAfterBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.insert_space_after_multiplicative_operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean wrapBeforeBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.wrap_before_multiplicative_operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int alignmentForBinaryExpression() {
			return LegacyFormatterOptions.this.delegate.alignment_for_multiplicative_operator;
		}

	}

	/**
	 * This is for when something is encountered that the legacy framework
	 * doesn't have support for; this class uses a combination of all possible
	 * options to determine what to do in such a case.
	 *
	 * @author isaki
	 */
	private final class CompositeBinaryOption implements LegacyBinaryOperatorFormatOption {

		CompositeBinaryOption() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean insertSpaceBeforeBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.insert_space_before_multiplicative_operator
				|| LegacyFormatterOptions.this.delegate.insert_space_before_additive_operator
				|| LegacyFormatterOptions.this.delegate.insert_space_before_string_concatenation
				|| LegacyFormatterOptions.this.delegate.insert_space_before_shift_operator
				|| LegacyFormatterOptions.this.delegate.insert_space_before_relational_operator
				|| LegacyFormatterOptions.this.delegate.insert_space_before_bitwise_operator
				|| LegacyFormatterOptions.this.delegate.insert_space_before_logical_operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean insertSpaceAfterBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.insert_space_after_multiplicative_operator
				|| LegacyFormatterOptions.this.delegate.insert_space_after_additive_operator
				|| LegacyFormatterOptions.this.delegate.insert_space_after_string_concatenation
				|| LegacyFormatterOptions.this.delegate.insert_space_after_shift_operator
				|| LegacyFormatterOptions.this.delegate.insert_space_after_relational_operator
				|| LegacyFormatterOptions.this.delegate.insert_space_after_bitwise_operator
				|| LegacyFormatterOptions.this.delegate.insert_space_after_logical_operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean wrapBeforeBinaryOperator() {
			return LegacyFormatterOptions.this.delegate.wrap_before_multiplicative_operator
				|| LegacyFormatterOptions.this.delegate.wrap_before_additive_operator
				|| LegacyFormatterOptions.this.delegate.wrap_before_string_concatenation
				|| LegacyFormatterOptions.this.delegate.wrap_before_shift_operator
				|| LegacyFormatterOptions.this.delegate.wrap_before_relational_operator
				|| LegacyFormatterOptions.this.delegate.wrap_before_bitwise_operator
				|| LegacyFormatterOptions.this.delegate.wrap_before_logical_operator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int alignmentForBinaryExpression() {
			return LegacyFormatterOptions.this.delegate.alignment_for_multiplicative_operator
				| LegacyFormatterOptions.this.delegate.alignment_for_additive_operator
				| LegacyFormatterOptions.this.delegate.alignment_for_string_concatenation
				| LegacyFormatterOptions.this.delegate.alignment_for_shift_operator
				| LegacyFormatterOptions.this.delegate.alignment_for_relational_operator
				| LegacyFormatterOptions.this.delegate.alignment_for_bitwise_operator
				| LegacyFormatterOptions.this.delegate.alignment_for_logical_operator;
		}

	}
}
