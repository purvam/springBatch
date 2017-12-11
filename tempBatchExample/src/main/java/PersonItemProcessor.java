
import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

	//@Override
	public Person process(Person person) throws Exception {
		// TODO Auto-generated method stub
		final String firstName = person.getFirstName().toUpperCase();
        final String lastName = person.getLastName().toUpperCase();

        final Person transformedPerson = new Person(firstName, lastName);

        //log.info("Converting (" + person + ") into (" + transformedPerson + ")");

        return transformedPerson;
	}
}
