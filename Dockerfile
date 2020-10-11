FROM g++:10
WORKDIR /app/
COPY ./* ./
RUN g++ Lexer.cpp -o Lexer.out
RUN chmod +x Lexer.out