/*
Author: Ashiqul Alam
Description: Create a processor in C; this will decode instruction into 32 bits,
assign the correct bits to the correct fields and set control wires in order
to do various instructions.
*/

#include "sim4.h"

WORD getInstruction(WORD curPC, WORD *instructionMemory)
{
    return instructionMemory[curPC/4]; //assuming word size is 4 bytes
}

void extract_instructionFields(WORD instruction, InstructionFields *fieldsOut)
{
    // Extract opcode, rs, rt, rd, shamt, and funct fields from the instruction
    fieldsOut->opcode = (instruction >> 26) & 0x3F;
    fieldsOut->rs = (instruction >> 21) & 0x1F; // reg 1
    fieldsOut->rt = (instruction >> 16) & 0x1F; // reg 2
    fieldsOut->rd = (instruction >> 11) & 0x1F; // destination reg
    fieldsOut->shamt = (instruction >> 6) & 0x1F; // shift ammount
    fieldsOut->funct = instruction & 0x3F; 

    // Extract imm16, imm32, and address fields
    fieldsOut->imm16 = instruction & 0xFFFF;
    fieldsOut->imm32 = signExtend16to32(fieldsOut->imm16);
    fieldsOut->address = instruction & 0x3FFFFFF; // 26-bit address
}

int fill_CPUControl(InstructionFields *fields, CPUControl *controlOut)
{
    // Extract opcode and funct fields from the InstructionFields struct
    int opcode = fields->opcode;
    int funct = fields->funct;

    // Reset all control bits to zero initially
    controlOut->ALUsrc = 0;
    controlOut->ALU.op = 0;
    controlOut->ALU.bNegate = 0;
    controlOut->memRead = 0;
    controlOut->memWrite = 0;
    controlOut->memToReg = 0;
    controlOut->regDst = 0;
    controlOut->regWrite = 0;
    controlOut->branch = 0;
    controlOut->jump = 0;
    controlOut->extra1 = 0; //extra1 is for instruction bne

    // Extra control bit for bne, inverts ALUResult.zero so that it works properly
    //controlOut->invertZero = 0;

    switch (opcode)
    {
    case 0: // indicates R-type instruction
        // Indicates that we use reg 2 and write register separately
        switch (funct)
        {
        // add
        case 32:
                controlOut->regDst = 1;
        controlOut->regWrite = 1;
            controlOut->ALU.op = 2;
            controlOut->ALU.bNegate = 0;
            return 1;
        // addu
        case 33:
                controlOut->regDst = 1;
        controlOut->regWrite = 1;
            controlOut->ALU.op = 2;
            controlOut->ALU.bNegate = 0;
            return 1;
        // sub
        case 34:
                controlOut->regDst = 1;
        controlOut->regWrite = 1;
            controlOut->ALU.op = 2;
            controlOut->ALU.bNegate = 1;
            return 1;
        // subu
        case 35:
                controlOut->regDst = 1;
        controlOut->regWrite = 1;
            controlOut->ALU.op = 2;
            controlOut->ALU.bNegate = 1;
            return 1;
        // and
        case 36:
                controlOut->regDst = 1;
        controlOut->regWrite = 1;
            controlOut->ALU.op = 0;
            controlOut->ALU.bNegate = 0;
            return 1;
        // or
        case 37:
                controlOut->regDst = 1;
        controlOut->regWrite = 1;
            controlOut->ALU.op = 1;
            controlOut->ALU.bNegate = 0;
            return 1;
        // xor
        case 38:
                controlOut->regDst = 1;
        controlOut->regWrite = 1;
            controlOut->ALU.op = 4;
            controlOut->ALU.bNegate = 0;
            return 1;

        // slt
        case 42:
                controlOut->regDst = 1;
        controlOut->regWrite = 1;
            controlOut->ALU.op = 3;
            controlOut->ALU.bNegate = 1;
            return 1;

        // other cases for additional ALU op, set to 0
        default:
            return 0;
        }
        // First three are the extra instructions

    case 12:                      // andi
        controlOut->ALUsrc = 1;   // ignore reg 2 data, imm32 read
        controlOut->regWrite = 1; // output written to rt
        //testing
        controlOut->extra2 = 1; // chooses between sign extended and zero extended imm field
        // ALUop for and
        controlOut->ALU.op = 0;
        controlOut->ALU.bNegate = 0;
        return 1;

    case 5:                       // bne
        controlOut->branch = 1;   

        // bne does subtraction and checks the 0/1 result.
        // if subtraction is 0, results are EQUAL, so it should not branch.
        // if subtraction is 1, results are NOT EQUAL, so it should branch.
        // create new control bit that does this, invertZero
        controlOut->extra1 = 1;

        // ALUop for sub
        controlOut->ALU.op = 2;
        controlOut->ALU.bNegate = 1;
        return 1;

    case 15:                      // lui
        controlOut->regWrite = 1; // store to register rt
        controlOut->ALUsrc = 1;   // ignore reg 2 data
        controlOut->extra3 = 1; // control bit for lui instruction
        return 1;


    case 8: // addi
        // for addi, reg 2 value gets ignored, we write to reg
        // ALU output goes to write data
        // We use the 16 bit immediate field
        // ALU result goes to write data

        controlOut->ALUsrc = 1;   //  32 bit imm field, reg 2 data is ignored
        controlOut->regWrite = 1; // output written to reg
        // ALUop for add
        controlOut->ALU.op = 2;
        controlOut->ALU.bNegate = 0;
        return 1;

    case 9: // addiu
        // same cases as addi
        controlOut->ALUsrc = 1;   //  32 bit imm field, reg 2 data is ignored
        controlOut->regWrite = 1; // output written to reg
        // ALUop for add
        controlOut->ALU.op = 2;
        controlOut->ALU.bNegate = 0;
        return 1;

    case 10:                      // slti
        controlOut->ALUsrc = 1;   // use the immediate
        controlOut->regWrite = 1; // send the result to a register
        // Set ALUop for slti
        controlOut->ALU.op = 3;
        controlOut->ALU.bNegate = 1;
        return 1;

    case 35:                      // lw
        controlOut->memToReg = 1; // ALU result is address, read from memory
        controlOut->memRead = 1;  // enable memRead
        controlOut->regWrite = 1; // store to register
        controlOut->ALUsrc = 1;   // ignore reg 2 data
        // lw performs add instruction
        controlOut->ALU.op = 2;
        controlOut->ALU.bNegate = 0;
        return 1;

    case 43:                      // sw
        controlOut->memWrite = 1; // enable memWrite
        controlOut->ALUsrc = 1;   // offset address, reg 2 is ignored
        // sw performs add instruction
        controlOut->ALU.op = 2;
        controlOut->ALU.bNegate = 0;
        return 1;

    case 4: // beq
        controlOut->branch = 1;
        // beq does subtraction, result should either be zero or somehting else
        controlOut->ALU.op = 2; 
        controlOut->ALU.bNegate = 1;
        return 1;

    case 2: // j
        controlOut->jump = 1;
        return 1;

    default:
        return 0;
    }
}
WORD getALUinput1(CPUControl *controlIn,
                  InstructionFields *fieldsIn,
                  WORD rsVal, WORD rtVal, WORD reg32, WORD reg33,
                  WORD oldPC)
{
    return rsVal;     
}

WORD getALUinput2(CPUControl *controlIn,
                  InstructionFields *fieldsIn,
                  WORD rsVal, WORD rtVal, WORD reg32, WORD reg33,
                  WORD oldPC)
{
    if (controlIn->ALUsrc == 0) {
        return rtVal;
        }
    else {
        if(controlIn->extra2 == 0) {
            return fieldsIn->imm32;
            }
            else {
            return fieldsIn->imm16 | 0x00000000;
            }
    }
}
void execute_ALU(CPUControl *controlIn,
                 WORD input1, WORD input2,
                 ALUResult *aluResultOut)
{
    // Check for bNegate 
    if (controlIn->ALU.bNegate == 1) {
        input2 = -input2;
    }
    // ALU has 5 operations, AND OR ADD LESS XOR
    if (controlIn->ALU.op == 0) {
        //AND
        aluResultOut->result = input1 & input2;
    }
    else if (controlIn->ALU.op == 1) {
        //OR
        aluResultOut->result = input1 | input2;
    }
    else if (controlIn->ALU.op == 2) {
        //ADD
        aluResultOut->result = input1 + input2;
    }
    else if (controlIn->ALU.op == 3) {
        //LESS
        int result = input1 + input2;
        if (result < 0) {
            aluResultOut->result = 1;

        }
        else if (result > 0) {
            aluResultOut->result = 0;

        }
    }
    else if (controlIn->ALU.op == 4) {
        //XOR
        aluResultOut->result = input1 ^ input2;

    }

    // Check if result is zero and set ALU.zero appropriately
        // Do the bne inversing here
        if (controlIn->extra1 == 0) {
            if (aluResultOut->result == 0) {
                aluResultOut->zero = 1;
            }
            else {
                aluResultOut->zero = 0;
            }
        }
        else {
            if (aluResultOut->result == 0) {
                aluResultOut->zero = 0;
            }
            else {
                aluResultOut->zero = 1;
            }
        }
}

void execute_MEM(CPUControl *controlIn,
                 ALUResult *aluResultIn,
                 WORD rsVal, WORD rtVal,
                 WORD *memory,
                 MemResult *resultOut)
{
    // ALU result becomes address
    // rtVal becomes Write Data, it is also used for MemtoReg MUX
    // R-formats write to rd, I-formats write to rt
    
    //sw
    if (controlIn->memWrite == 1) {
        memory[aluResultIn->result/4] = rtVal;
        resultOut->readVal = 0;
    }
    //lw
    else if (controlIn->memRead == 1) {
        if (controlIn->extra3 == 0){
            resultOut->readVal = memory[aluResultIn->result/4]; 
        }
        else {
            resultOut->readVal = memory[aluResultIn->result>>4];
        }
    }
    // all other instructions
    else {
        resultOut->readVal = 0;
    }
    }

WORD getNextPC(InstructionFields *fields, CPUControl *controlIn, int aluZero,
               WORD rsVal, WORD rtVal,
               WORD oldPC)
{
    // Check if we need to jump
    if (controlIn->jump == 1) {
        // New PC is the target address
        return (oldPC & 0xf0000000) | (fields->address << 2);
    } else if (controlIn->branch == 1) {
        // Branch instruction: PC + 4 or branch target depending on aluZero
        if (aluZero == 1) {
            // Branch taken: New PC is branch target address
            return oldPC + 4 + (fields->imm32 << 2);
        } else {
            // Branch not taken: PC + 4
            return oldPC + 4;
        }
    } else {
        // Other instructions: PC + 4
        return oldPC + 4;
    }
}

void execute_updateRegs(InstructionFields *fields, CPUControl *controlIn,
                        ALUResult *aluResultIn, MemResult *memResultIn,
                        WORD *regs)
{
    // Determine which register to write to based on control signals
    WORD regToWrite;
    if (controlIn->regDst == 1) {
        regToWrite = fields->rd;
    } else {
        regToWrite = fields->rt;
    }
    
    // Update register value based on control signals
    if (controlIn->regWrite == 1) {
        if (controlIn->memToReg == 1) {
            // Write data from memory
            regs[regToWrite] = memResultIn->readVal;
        } else {
            if (controlIn->extra3 == 0) {
            // Write data from ALU
            regs[regToWrite] = aluResultIn->result;
            }
            else {
                regs[regToWrite] = fields->imm16 <<16;
            }
        }
    }
}
