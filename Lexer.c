#include <stdio.h>
#include <string.h>
// #define DEBUG
#define MAX_LEN 1009
#define STACK_LEN 1009
#define TER_SYM_NUM 6

int ptr;
char topTerSym;
char str[MAX_LEN], terSym[TER_SYM_NUM + 1] = "+*i()#";
int preMat[6][6] = {
    {1, -1, -1, -1, 1, 1},
    {1, 1, -1, -1, 1, 1},
    {1, 1, 0, 0, 1, 1},
    {-1, -1, -1, -1, 2, 0},
    {1, 1, 0, 0, 1, 1},
    {-1, -1, -1, -1, 0, 2}}; // -1 below  1 above  0 undefined  2 equal
struct CHAR
{
    int type; // 0 non-terminal  1 terminal
    char ch;
} stack[1009];

void updateTopTerSym()
{
    int i;
    for (i = ptr - 1; i >= 0; i--)
        if (stack[i].type == 1)
        {
            topTerSym = stack[i].ch;
            break;
        }
}
int getTerSymNum(char ch)
{
    int i;
    for (i = 0; i < TER_SYM_NUM; i++)
        if (terSym[i] == ch)
            return i;
    return -1;
}
int cmpTerSym(char ca, char cb)
{
    int na = getTerSymNum(ca), nb = getTerSymNum(cb);
    if (na == -1 || nb == -1) // ca or cb isn't a terminal symbol
        return -2;
    return preMat[na][nb];
}
void push(int type, char ch)
{
    stack[ptr].type = type;
    stack[ptr].ch = ch;
    ptr++;
}
int pop(int *type, char *ch)
{
    if (--ptr == 0) // empty stack
        return -1;
    *type = stack[ptr].type;
    *ch = stack[ptr].ch;
    return 0;
}
int reduce()
{
    int type;
    char ch;
    if (pop(&type, &ch) != 0)
        return -1;
    if (type == 0) // non-terminal
    {
        if (pop(&type, &ch) != 0 || type == 0)
            return -1;
        if (ch == '+' || ch == '*')
        {
            if (pop(&type, &ch) != 0 || type == 1)
                return -1;
            push(0, 'N'); // N+N or N*N
            return 0;
        }
        else
            return -1;
    }
    else if (type == 1) // terminal
    {
        if (ch == ')')
        {
            if (pop(&type, &ch) != 0 || type == 1)
                return -1;
            if (pop(&type, &ch) != 0 || type == 0 || ch != '(')
                return -1;
            push(0, 'N'); // (N)
            return 0;
        }
        else if (ch == 'i')
        {
            push(0, 'N'); // i
            return 0;
        }
        else
            return -1;
    }
}
void solve()
{
    strcat(str, "#");
    int sptr = 0, len = strlen(str);
    push(1, '#');
    updateTopTerSym();
    while (sptr < len)
    {
        char sign = str[sptr];
        if (sign == '#' && sptr != len - 1)
        {
            printf("E\n");
            break;
        }
        int cmpRes = cmpTerSym(topTerSym, sign);
        if (cmpRes == -2 || cmpRes == 0)
        {
            printf("E\n");
            break;
        }
        else if (cmpRes == -1)
        {
            push(1, sign);
            sptr++;
            updateTopTerSym();
            printf("I%c\n", sign);
        }
        else if (cmpRes == 1 || (cmpRes == 2 && sign == ')'))
        {
            if (reduce() == 0)
            {
                printf("R\n");
                updateTopTerSym();
            }
            else
            {
                printf("RE\n");
                break;
            }
        }
        else if (cmpRes == 2 && sign == '#')
            sptr++;
    }
}

#ifndef DEBUG
int main(int argc, char *argv[])
{
    freopen(argv[1], "r", stdin);
    scanf("%s", str);
    solve();
    fclose(stdin);
    return 0;
}
#endif

#ifdef DEBUG
int main()
{
    scanf("%s", str);
    solve();
    return 0;
}
#endif