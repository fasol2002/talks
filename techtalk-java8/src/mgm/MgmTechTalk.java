package mgm;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import mgm.model.PimItemCombinationModel;
import mgm.model.PimPromotionPrintProjectModel;
import mgm.model.ProductModel;

public class MgmTechTalk {

	public static void main(String[] args) throws Exception {
		// In this talk:
		// [x] Lambda
		// [x] Method reference
		// [x] Stream
		// [ ] Default method
		// [ ] java.time
		// [ ] java.nio
		 
		stream4();
	}
	
	public static void lambda1()  {
		
		// Lambda are anonymous method with no name
		// return and { } can be skipped if only one statement
		Supplier<String> supplier1 = () -> "I'm a lambda";
		Runnable runnable1 = () ->  System.out.println("I'm a runnable"); 
		// only executed once their 'main' method is called
		System.out.println(supplier1.get());
		runnable1.run();
		
		// They can be of different types Supplier, Function, Consumer, BiFunction, Runnable, ...
		// Interface will have @java.lang.FunctionalInterface
		Function<String, String> function1 = (s1) -> s1.toUpperCase();
		// You can design you own FunctionalInterface
	}
	
	public static void lambda2()  {	
		BiFunction<String, String, String> bifunction1 = (s1, s2) -> s1 + s2;
		BiFunction<Integer, Integer, Integer> bifunction2 = (s1, s2) -> s1 + s2;
		
		// First pitfall: Too big lambda
		BiFunction<String, String, String> bifunction3 = (s1, s2) -> {
			//
			// dozen lines of code
			//
			return "I am a long bifunction:" + s1 + " - " + s2;
		};
		
		//
		//
		//
		//
		//
		// Instead use method reference
		bifunction3 = MgmTechTalk::lambda2BiFunction;
		bifunction2 = MgmTechTalk::lambda2BiFunction;
		
		
		// Second pitfall: careful with method overloading
		// => write good method name or make sure to know the lambda's type
		System.out.println(bifunction3.apply("that", "this"));
		System.out.println(bifunction2.apply(4, 6));
	}
	
	// 	
	//	
	//	
	//	
	//	
	//	
	public static String lambda2BiFunction(String s1, String s2){
		//
		// dozen lines of code
		//
		return "I am a long bifunction:" + s1 + " - " + s2;
	}
	
	// 	
	//	
	//	
	//	
	//	
	//
	public static Integer lambda2BiFunction(Integer i1, Integer i2){
		//
		// dozen lines of code
		//
		return i1 + i2;
	}
	
	
	public static void stream1()  {
		// Javadoc: A sequence of elements supporting sequential and parallel aggregate operations.
		
		// Starts with stream factories
		List<String> res = Stream.of("mgm","tech", "talk", "talk")
				// intermediate operations
				.peek(System.out::println)
				.filter(w -> w.startsWith("t"))
				.peek(System.out::println)
				.map(String::toUpperCase)
				.peek(System.out::println)
				.distinct()
				// one terminal operation
				.collect(Collectors.toList());
		
		// Debug
		// .map(String::toUpperCase) 
		// .peek(System.out::println)
		
		System.out.println(res);
	}
	
	public static void stream2() throws URISyntaxException, IOException  {
		// new java.nio API
		Path path = Paths.get(ClassLoader.getSystemResource("firstname1920.data").toURI());
		
		// Get the most common female firstname
		Optional<Person> res = Files.lines(path) // Files stream factories
				.skip(1)
				.map(line -> new Person(line))
				.filter(person -> Objects.equals(person.getSex(),"F"))
				.parallel()
				// new Comparator Factory API
				.max(Comparator.comparing(Person::getCount));
		
		// new Optional API
		res.ifPresent(System.out::println);
	}
	
	public static void stream3() {
		IntStream.range(0, 8)
				// This return a stream that will be processed with the CommonForkJoinPool
				// default pool size = nb cores 
				.parallel()
				// Javadoc: This operation does not guarantee to respect the encountered order of the stream
				.forEach(i -> {
					System.out.println( i + ": " + Thread.currentThread());
				});
		// Pitfalls: In web application, most of the business code is already splitted to different thread
		// Using parallel stream would create a bottle neck as the pool is also used for other ForkPoolTasks
		// that are in competition with stream API executing/joining
		
	}
	
	public static void stream4() {	
		System.out.println(IntStream.range(0, 50).parallel()
			// Javadoc: Performs a mutable reduction operation on the elements of this stream
			.collect(ArrayList::new, ArrayList::add, ArrayList::addAll));
			
			// Even if multi-threaded, collect/reduce preserves the stream order
			//			.collect(ArrayList::new, (l,i)->{
			//				System.out.println( i + ": " +Thread.currentThread());
			//				l.add(i);
			//			}, ArrayList::addAll));
		
			// Javadoc: Performs a reduction on the elements of this stream, 
			// using the provided identity value and an associative accumulation function = (x ^ y) ^ z = x ^ (y ^ z)
			//.reduce(0, Integer::sum));
	}
	
	public static void stream5(){
		ProductModel product = null;
		
		Set<PimItemCombinationModel> items = getPimPromotionItemDao().findPromotionItemsForProduct(product).stream().map(promoItem -> promoItem.getPromotionProjects())
		.flatMap(projects -> projects.stream()).filter(project -> project instanceof PimPromotionPrintProjectModel)
		.map(PimPromotionPrintProjectModel.class::cast).flatMap(printProject -> printProject.getItemCombinations().stream())
		.sorted(Comparator.comparing(PimItemCombinationModel::getCode)).collect(Collectors.toSet());
		
		
		// It is important to indent and document such concise piece of code.
		
		// Get sorted product item-combinations
		items = getPimPromotionItemDao().findPromotionItemsForProduct(product).stream()
				// get promotion's projects
				.map(promoItem -> promoItem.getPromotionProjects())
				.flatMap(projects -> projects.stream())
				// keep only the 'print' subtype
				.filter(PimPromotionPrintProjectModel.class::isInstance)
				.map(PimPromotionPrintProjectModel.class::cast)
				// get print-project's item-combinations and sort them by code
				.flatMap(printProject -> printProject.getItemCombinations().stream())
				.sorted(Comparator.comparing(PimItemCombinationModel::getCode))
				// collect those
				.collect(Collectors.toSet());
		
		// But more important: this could have been done with a Dao
		// No matter how powerful the Stream API is think to what is executed.
	}
	
	//
	//
	//
	//
	//
	public static PimPromotionItemDao getPimPromotionItemDao(){
		return null;
	}
	
}
