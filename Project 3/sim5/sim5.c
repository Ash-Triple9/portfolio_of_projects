/*
Author: Ashiqul Alam
Description: Create a processor in C; this will decode instruction into 32 bits,
assign the correct bits to the correct fields and set control wires in order
to do various instructions. This time we are using pipelining to enhance our processor
*/
#include "sim5.h"

void extract_instructionFields(WORD instruction, InstructionFields *fieldsOut)
{
    // Extract opcode, rs, rt, rd, shamt, and funct fields from the instruction
    fieldsOut->opcode = (instruction >> 26) & 0x3F;
    fieldsOut->rs = (instruction >> 21) & 0x1F;   // reg 1
    fieldsOut->rt = (instruction >> 16) & 0x1F;   // reg 2
    fieldsOut->rd = (instruction >> 11) & 0x1F;   // destination reg
    fieldsOut->shamt = (instruction >> 6) & 0x1F; // shift ammount
    fieldsOut->funct = instruction & 0x3F;

    // Extract imm16, imm32, and address fields
    fieldsOut->imm16 = instruction & 0xFFFF;
    fieldsOut->imm32 = signExtend16to32(fieldsOut->imm16);
    fieldsOut->address = instruction & 0x3FFFFFF; // 26-bit address
}

int IDtoIF_get_stall(InstructionFields *fields,
                     ID_EX *old_idex, EX_MEM *old_exmem)
{
    // sw hazard detection
    if (fields->opcode == 43)
    { // curr instr is sw
        int fCheck = 0;
        if (old_idex->regWrite == 1)
        {
            if (old_idex->regDst == 1 && old_idex->rd == fields->rt)
            {
                fCheck = 1;
            }
            else if (old_idex->regDst == 0 && old_idex->rt == fields->rt)
            {
                fCheck = 1;
            }
        }
        if (fCheck == 1)
        {
            return 0;
        }

        if (old_exmem->regWrite == 1)
        {
            if (old_exmem->writeReg == fields->rt)
            {
                return 1;
            }
        }
    }
    // lw hazard detection
    if (old_idex->memRead == 0)
    {
        return 0;
    }
    else
    {
        int opcode = fields->opcode;
        switch (opcode)
        {
        case 0: // indicates R type format
            if (old_idex->rt == fields->rt || old_idex->rt == fields->rs)
            {
                return 1;
            }
        case 2:  // j
        case 15: // lui
            return 0;
        case 4: // beq
        case 5: // bne
            if (old_idex->rt == fields->rt || old_idex->rt == fields->rs)
            {
                return 1;
            }
        case 12:
        case 13:
        case 8:
        case 9:
        case 10:
        case 35:
        case 43:
            if (old_idex->rt == fields->rs)
            {
                return 1;
            }
        default:
            return 0;
        }
    }
}

int IDtoIF_get_branchControl(InstructionFields *fields, WORD rsVal, WORD rtVal)
{
    if (fields->opcode == 0 && fields->funct == 0)
    {
        return 0;
    }
    if ((fields->opcode == 5 && ((rsVal - rtVal) != 0)) || (fields->opcode == 4 && ((rsVal - rtVal) == 0)))
    {
        return 1;
    }
    if (fields->opcode == 2)
    {
        return 2;
    }
    return 0;
}

WORD calc_branchAddr(WORD pcPlus4, InstructionFields *fields)
{
    return pcPlus4 + (fields->imm32 << 2);
}
WORD calc_jumpAddr(WORD pcPlus4, InstructionFields *fields)
{
    return (pcPlus4 & 0xf0000000) | (fields->address << 2);
}

int execute_ID(int IDstall,
               InstructionFields *fieldsIn,
               WORD pcPlus4,
               WORD rsVal, WORD rtVal,
               ID_EX *new_idex)
{

    // set all the control bits
    int opcode = fieldsIn->opcode;
    int funct = fieldsIn->funct;

    // initially set all of them to 0
    new_idex->ALUsrc = 0;
    new_idex->ALU.op = 0;
    new_idex->ALU.bNegate = 0;
    new_idex->memRead = 0;
    new_idex->memWrite = 0;
    new_idex->memToReg = 0;
    new_idex->regDst = 0;
    new_idex->regWrite = 0;
    new_idex->rsVal = rsVal;
    new_idex->rtVal = rtVal;
    new_idex->rs = fieldsIn->rs;
    new_idex->rt = fieldsIn->rt;
    new_idex->rd = fieldsIn->rd;
    new_idex->imm16 = fieldsIn->imm16;
    new_idex->imm32 = fieldsIn->imm32;
    new_idex->extra1 = 0;
    new_idex->extra2 = 0;
    new_idex->extra3 = 0;

    if (IDstall == 1)
    {
        new_idex->rsVal = 0;
        new_idex->rtVal = 0;
        new_idex->rs = 0;
        new_idex->rt = 0;
        new_idex->rd = 0;
        new_idex->imm16 = 0;
        new_idex->imm32 = 0;
    }
    switch (opcode)
    {
    case 0: // indicates funct
        switch (funct)
        {
        // sll/NOP
        case 0:
            //////printf("This is NOP\n");
            new_idex->regDst = 1;
            new_idex->regWrite = 1;
            new_idex->ALU.op = 5;
            ////printf("I am returning\n");
            return 1;
            //////printf("I couldn't\n");
        // add
        case 32:
            if (!(IDstall == 1))
            {
                new_idex->regDst = 1;
                new_idex->regWrite = 1;
                new_idex->ALU.op = 2;
                new_idex->ALU.bNegate = 0;
            }
            return 1;
        // addu
        case 33:
            if (!(IDstall == 1))
            {
                new_idex->regDst = 1;
                new_idex->regWrite = 1;
                new_idex->ALU.op = 2;
                new_idex->ALU.bNegate = 0;
            }
            return 1;
        // sub
        case 34:
            if (!(IDstall == 1))
            {
                new_idex->regDst = 1;
                new_idex->regWrite = 1;
                new_idex->ALU.op = 2;
                new_idex->ALU.bNegate = 1;
            }
            return 1;
        // subu
        case 35:
            if (!(IDstall == 1))
            {
                new_idex->regDst = 1;
                new_idex->regWrite = 1;
                new_idex->ALU.op = 2;
                new_idex->ALU.bNegate = 1;
            }
            return 1;
        // and
        case 36:
            if (!(IDstall == 1))
            {
                new_idex->regDst = 1;
                new_idex->regWrite = 1;
                new_idex->ALU.op = 0;
                new_idex->ALU.bNegate = 0;
            }
            return 1;
        // or
        case 37:
            if (!(IDstall == 1))
            {
                new_idex->regDst = 1;
                new_idex->regWrite = 1;
                new_idex->ALU.op = 1;
                new_idex->ALU.bNegate = 0;
            }
            return 1;
        // xor
        case 38:
            if (!(IDstall == 1))
            {
                new_idex->regDst = 1;
                new_idex->regWrite = 1;
                new_idex->ALU.op = 4;
                new_idex->ALU.bNegate = 0;
            }
            return 1;

        // nor
        case 39:
            if (!(IDstall == 1))
            {
                new_idex->regDst = 1;
                new_idex->regWrite = 1;
                new_idex->ALU.op = 0;
                new_idex->ALU.bNegate = 0;
                new_idex->extra1 = 1;
            }
            return 1;

        // slt
        case 42:
            if (!(IDstall == 1))
            {
                new_idex->regDst = 1;
                new_idex->regWrite = 1;
                new_idex->ALU.op = 3;
                new_idex->ALU.bNegate = 1;
            }
            return 1;

        // other cases for additional ALU op, set to 0
        default:
            return 0;
        }
        // First three are the extra instructions

    case 12: // andi
        if (!(IDstall == 1))
        {
            new_idex->ALUsrc = 2;   // ignore reg 2 data, imm32 read
            new_idex->regWrite = 1; // output written to rt
                                    // ALUop for and
            new_idex->ALU.op = 0;
            new_idex->ALU.bNegate = 0;
        }
        return 1;

    case 5: // bne
            // bne does subtraction and checks the 0/1 result.
            // if subtraction is 0, results are EQUAL, so it should not branch.
            // if subtraction is 1, results are NOT EQUAL, so it should branch.
            // create new control bit that does this, invertZero
        new_idex->rsVal = 0;
        new_idex->rtVal = 0;
        new_idex->rs = 0;
        new_idex->rt = 0;
        new_idex->rd = 0;
        new_idex->imm16 = 0;
        new_idex->imm32 = 0;
        return 1;

    case 13:
        if (!(IDstall == 1))
        {
            // ori
            new_idex->ALUsrc = 2;   // ignore reg 2 data, imm32 read
            new_idex->regWrite = 1; // output written to rt
            // ALUop for or
            new_idex->ALU.op = 1;
            new_idex->ALU.bNegate = 0;
        }
        return 1;

    case 15: // lui
        if (!(IDstall == 1))
        {
            new_idex->regWrite = 1; // store to register rt
            new_idex->ALUsrc = 1;   // ignore reg 2 data
            new_idex->extra2 = 1;   // this control bit will shift the imm 16 left by 16
            new_idex->extra3 = fieldsIn->imm16 << 16;
        }
        return 1;

    case 8: // addi
            // for addi, reg 2 value gets ignored, we write to reg
            // ALU output goes to write data
            // We use the 16 bit immediate field
            // ALU result goes to write data
        if (!(IDstall == 1))
        {
            new_idex->ALUsrc = 1;   //  32 bit imm field, reg 2 data is ignored
            new_idex->regWrite = 1; // output written to reg
            // ALUop for add
            new_idex->ALU.op = 2;
            new_idex->ALU.bNegate = 0;
        }
        return 1;

    case 9: // addiu
        if (!(IDstall == 1))
        {
            // same cases as addi
            new_idex->ALUsrc = 1;   //  32 bit imm field, reg 2 data is ignored
            new_idex->regWrite = 1; // output written to reg
            // ALUop for add
            new_idex->ALU.op = 2;
            new_idex->ALU.bNegate = 0;
        }
        return 1;

    case 10: // slti
        if (!(IDstall == 1))
        {
            new_idex->ALUsrc = 1;   // use the immediate
            new_idex->regWrite = 1; // send the result to a register
            // Set ALUop for slti
            new_idex->ALU.op = 3;
            new_idex->ALU.bNegate = 1;
        }
        return 1;

    case 35: // lw
        if (!(IDstall == 1))
        {
            new_idex->memToReg = 1; // ALU result is address, read from memory
            new_idex->memRead = 1;  // enable memRead
            new_idex->regWrite = 1; // store to register
            new_idex->ALUsrc = 1;   // ignore reg 2 data
            // lw performs add instruction
            new_idex->ALU.op = 2;
            new_idex->ALU.bNegate = 0;
        }
        return 1;

    case 43: // sw
        if (!(IDstall == 1))
        {
            new_idex->memWrite = 1; // enable memWrite
            new_idex->ALUsrc = 1;   // offset address, reg 2 is ignored
            // sw performs add instruction
            new_idex->ALU.op = 2;
            new_idex->ALU.bNegate = 0;
        }
        return 1;

    case 4: // beq
        new_idex->rsVal = 0;
        new_idex->rtVal = 0;
        new_idex->rs = 0;
        new_idex->rt = 0;
        new_idex->rd = 0;
        new_idex->imm16 = 0;
        new_idex->imm32 = 0;
        return 1;

    case 2: // j
        new_idex->rsVal = 0;
        new_idex->rtVal = 0;
        new_idex->rs = 0;
        new_idex->rt = 0;
        new_idex->rd = 0;
        new_idex->imm16 = 0;
        new_idex->imm32 = 0;
        return 1;

    default:
        return 0;
    }
}

WORD EX_getALUinput1(ID_EX *in, EX_MEM *old_exMem, MEM_WB *old_memWb)
{
    if (old_exMem->regWrite && (old_exMem->writeReg == in->rs))
    {
        return old_exMem->aluResult;
    }
    else if (old_memWb->regWrite)
    {
        if (!(old_memWb->memToReg) && (old_memWb->writeReg == in->rs))
        {
            return old_memWb->aluResult;
        }
        else if ((old_memWb->memToReg) && (old_memWb->writeReg == in->rs))
        {
            return old_memWb->memResult;
        }
        else
            return in->rsVal;
    }
    else
        return in->rsVal;
}
WORD EX_getALUinput2(ID_EX *in, EX_MEM *old_exMem, MEM_WB *old_memWb)
{
    if (in->ALUsrc == 0)
    // this indicates that instruction is R format and we should check for
    // potential forwarding
    {
        if (old_exMem->regWrite && (old_exMem->writeReg == in->rt))
        {
            return old_exMem->aluResult;
        }
        else if (old_memWb->regWrite)
        {
            if (!(old_memWb->memToReg) && (old_memWb->writeReg == in->rt))
            {
                return old_memWb->aluResult;
            }
            else if ((old_memWb->memToReg) && (old_memWb->writeReg == in->rt))
            {
                return old_memWb->memResult;
            }
            else
                return in->rtVal;
        }
        else
            return in->rtVal;
    }
    else if (in->ALUsrc == 1)
    // I format
    {
        return in->imm32;
    }

    else
    // zero extended result
    {
        return (in->imm16 | 0x00000000);
    }
}

void execute_EX(ID_EX *in, WORD input1, WORD input2,
                EX_MEM *new_exMem)
{
    // forward all the relevant control bits
    new_exMem->memRead = in->memRead;
    new_exMem->memWrite = in->memWrite;
    new_exMem->memToReg = in->memToReg;
    new_exMem->regWrite = in->regWrite;
    new_exMem->rtVal = in->rtVal;
    new_exMem->rt = in->rt;
    new_exMem->extra1 = in->extra1;
    new_exMem->extra2 = in->extra2;
    new_exMem->extra3 = in->extra3;

    if (in->ALUsrc == 0)
    { // instruction is R format, use rd
        new_exMem->writeReg = in->rd;
    }
    else if (in->ALUsrc == 1 || in->ALUsrc == 2)
    { // I format, use rt
        new_exMem->writeReg = in->rt;
    }

    // Check for bNegate
    if (in->ALU.bNegate == 1)
    {
        input2 = -input2;
    }
    if (in->extra1 == 1)
    {
        input1 = ~input1;
        input2 = ~input2;
    }

    // ALU has 6 operations, AND OR ADD LESS XOR NOP
    if (in->ALU.op == 0)
    {
        // AND
        new_exMem->aluResult = input1 & input2;
    }
    else if (in->ALU.op == 1)
    {
        // OR
        new_exMem->aluResult = input1 | input2;
    }
    else if (in->ALU.op == 2)
    {
        // ADD
        new_exMem->aluResult = input1 + input2;
    }
    else if (in->ALU.op == 3)
    {
        // LESS
        int result = input1 + input2;
        if (result < 0)
        {
            new_exMem->aluResult = 1;
        }
        else if (result > 0)
        {
            new_exMem->aluResult = 0;
        }
        else
        {
            new_exMem->aluResult = 0;
        }
    }
    else if (in->ALU.op == 4)
    {
        // XOR
        new_exMem->aluResult = input1 ^ input2;
    }
    else if (in->ALU.op == 5)
    {
        /// NOP
        new_exMem->aluResult = 0;
    }
}

void execute_MEM(EX_MEM *in, MEM_WB *old_memWb,
                 WORD *mem, MEM_WB *new_memwb)
{
    new_memwb->memToReg = in->memToReg;
    new_memwb->writeReg = in->writeReg;
    new_memwb->regWrite = in->regWrite;
    new_memwb->aluResult = in->aluResult;
    new_memwb->extra1 = in->extra1;
    new_memwb->extra2 = in->extra2;
    new_memwb->extra3 = in->extra3;

    // sw forward checking
    if (in->memWrite == 1 && in->memRead == 0)
    { // checks if curr instr is sw
        if (old_memWb->regWrite == 1)
        {
            if (old_memWb->writeReg == in->rt)
            {
                if (old_memWb->memToReg == 1)
                {
                    mem[in->aluResult / 4] = old_memWb->memResult;
                }
                else if (old_memWb->memToReg == 0)
                {
                    mem[in->aluResult / 4] = old_memWb->aluResult;
                }
            }
            else
            {
                mem[in->aluResult / 4] = in->rtVal;
            }
        }
        else
        {
            mem[in->aluResult / 4] = in->rtVal;
        }
        new_memwb->memResult = 0;
    }
    // lw
    else if (in->memRead == 1 && in->memWrite == 0)
    {
        new_memwb->memResult = mem[in->aluResult / 4];
    }
    else if (in->memRead == 0 && in->memWrite == 0)
    { // all other instructions
        new_memwb->memResult = 0;
    }
}

void execute_WB(MEM_WB *in, WORD *regs)
{
    if (in->regWrite)
    {
        if (in->extra2 == 0)
        {
            if (in->memToReg == 1)
            {
                regs[in->writeReg] = in->memResult;
            }
            else if (in->memToReg == 0)
            {
                regs[in->writeReg] = in->aluResult;
            }
        }
        else if (in->extra2 == 1)
        {
            regs[in->writeReg] = in->extra3;
        }
    }
}
