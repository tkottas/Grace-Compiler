fun main () : nothing
   var seed, i : int;
   var x : int[16];
   var c,e : char;
   var d : char[15];
{
	c <- 'c';
	putc(c);
	e <- 'l';
	putc(e);
	c <- e;
	putc(c);
	d[13] <- 'x';
	putc(d[13]);
	c <- "mitsos"[3];
	putc(c);
}
