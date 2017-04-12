package Compiler.FrontEnd.AbstractSyntaxTree.Expression.UnaryExpression;

import Compiler.BackEnd.ControlFlowGraph.Instruction.ArithmeticInstruction.BinaryInstruction.AdditionInstruction;
import Compiler.BackEnd.ControlFlowGraph.Instruction.Instruction;
import Compiler.BackEnd.ControlFlowGraph.Instruction.MemoryInstruction.MoveInstruction;
import Compiler.BackEnd.ControlFlowGraph.Instruction.MemoryInstruction.StoreInstruction;
import Compiler.BackEnd.ControlFlowGraph.Operand.Address;
import Compiler.BackEnd.ControlFlowGraph.Operand.ImmediateValue;
import Compiler.Environment.Environment;
import Compiler.FrontEnd.AbstractSyntaxTree.Expression.Expression;
import Compiler.FrontEnd.AbstractSyntaxTree.Type.BasicType.IntType;
import Compiler.FrontEnd.AbstractSyntaxTree.Type.Type;
import Compiler.Utility.Error.CompilationError;

import java.util.List;

public class PostfixIncrementExpression extends UnaryExpression {
  private PostfixIncrementExpression(Type type, boolean isLeftValue, Expression expression) {
    super(type, isLeftValue, expression);
  }

  public static Expression getExpression(Expression expression) {
    if (!expression.isLeftValue) {
      throw new CompilationError("a left-value expression is expected in the postfix-increment expression");
    }
    if (expression.type instanceof IntType) {
      return new PostfixIncrementExpression(IntType.getType(), false, expression);
    }
    throw new CompilationError("an int-type expression is expected in the postfix-increment expression");
  }

  @Override
  public String toString() {
    return "[expression: postfix-increment]";
  }

  @Override
  public void emit(List<Instruction> instructions) {
    expression.emit(instructions);
    operand = Environment.registerTable.addTemporaryRegister();
    if (expression.operand instanceof Address) {
      Address address = (Address) expression.operand;
      address = new Address(address.base, address.offset, address.size);
      expression.load(instructions);
      instructions.add(MoveInstruction.getInstruction(operand, expression.operand));
      instructions.add(AdditionInstruction.getInstruction(expression.operand, expression.operand, new ImmediateValue(1)));
      instructions.add(StoreInstruction.getInstruction(expression.operand, address));
    } else {
      expression.load(instructions);
      instructions.add(MoveInstruction.getInstruction(operand, expression.operand));
      instructions.add(AdditionInstruction.getInstruction(expression.operand, expression.operand, new ImmediateValue(1)));
    }
  }
}
