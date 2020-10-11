#include <cstdio>
#include <cstring>
#include <cctype>
#include <cstdlib>
using namespace std;
const int MAX_LEN = 100;
FILE *fp;
int len;
char ch;
char token[MAX_LEN];
void addNul()
{
    token[len] = '\0';
}
void getch()
{
    ch = fgetc(fp);
}
void cat()
{
    token[len++] = ch;
}
void ungetch()
{
    ungetc(ch, fp);
}
void reserve()
{
    addNul();
    if (strcmp(token, "BEGIN") == 0)
        printf("Begin\n");
    else if (strcmp(token, "END") == 0)
        printf("End\n");
    else if (strcmp(token, "FOR") == 0)
        printf("For\n");
    else if (strcmp(token, "IF") == 0)
        printf("If\n");
    else if (strcmp(token, "THEN") == 0)
        printf("Then\n");
    else if (strcmp(token, "ELSE") == 0)
        printf("Else\n");
    else
        printf("Ident(%s)\n", token);
}
void clearToken()
{
    len = 0;
    memset(token, 0, sizeof(token));
}
int getNumber()
{
    addNul();
    return atoi(token);
}
void getsym()
{
    clearToken();
    do
        getch();
    while (isspace(ch));
    if (isalpha(ch))
    {
        while (isalpha(ch) || isdigit(ch))
        {
            cat();
            getch();
        }
        ungetch();
        reserve();
    }
    else if (isdigit(ch))
    {
        while (isdigit(ch))
        {
            cat();
            getch();
        }
        ungetch();
        printf("Int(%d)\n", getNumber());
    }
    else if (ch == ':')
    {
        getch();
        if (ch == '=')
            printf("Assign\n");
        else
        {
            ungetch();
            printf("Colon\n");
        }
    }
    else if (ch == '+')
        printf("Plus\n");
    else if (ch == '*')
        printf("Star\n");
    else if (ch == ',')
        printf("Comma\n");
    else if (ch == '(')
        printf("LParenthesis\n");
    else if (ch == ')')
        printf("RParenthesis\n");
    else if (!feof(fp))
        printf("Unknown\n");
}
int main(int argc, char *argv[])
{
    fp = fopen(argv[1], "r");
    while (!feof(fp))
        getsym();
    fclose(fp);
    return 0;
}