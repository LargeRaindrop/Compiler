FROM gcc:10
WORKDIR /app/
COPY ./* ./
RUN gcc Lexer.c -o Lexer.out
RUN chmod +x Lexer.out