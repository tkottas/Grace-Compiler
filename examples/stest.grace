fun main () : nothing
   fun bsort (n : int; ref x : int[]) : nothing
      fun swap (ref x, y : int) : nothing
         var t : int;
      {
         t <- x;
         x <- y;
         y <- t;
      }
      var changed, i : int;
   {
      changed <- 1;
      while changed > 0 do {
        changed <- 0;
        i <- 0;
        while i < n-1 do {
          if x[i] > x[i+1] then {
            swap(x[i],x[i+1]);
            changed <- 1;
          }
          i <- i+1;
        }
      }
   }

   fun putArray (n : int; ref x : int[]) : nothing
      var i : int;
   {
      i <- 0;
      while i < n do {
        puti(x[i]);
        i <- i+1;
      }
   }

   var seed, i : int;
   var x : int[16];
{
  seed <- 65;
  i <- 0;
  while i < 16 do {
    seed <- (seed * 137 + 220 + i) mod 101;
    x[i] <- seed;
    i <- i+1;
  }
  puti(x[5]);
  putArray(16, x);
  bsort(16,x);
  putArray(16, x);

}
