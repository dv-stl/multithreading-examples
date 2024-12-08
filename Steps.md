# Multithreading examples 

1. Unsafe sequence
- run few times until duplicated number is found
- it fails because ++ is read, increment and write operation
- show it can work with properly synchronized counters

2. VisibilityAndVolatile
- run few times to see different output
- problem is that local variable may be cached and not synchronized with mine memory 
- volatile in magic container can solve the problem 