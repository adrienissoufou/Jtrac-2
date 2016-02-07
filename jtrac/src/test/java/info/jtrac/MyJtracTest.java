package info.jtrac;

import java.util.HashSet;
import java.util.Set;

import org.springframework.transaction.support.TransactionSynchronizationManager;

import info.jtrac.domain.Item;
import info.jtrac.domain.ItemUser;
import info.jtrac.domain.Metadata;
import info.jtrac.domain.Space;
import info.jtrac.domain.State;
import info.jtrac.domain.User;

public class MyJtracTest extends JtracTestBase {

	public MyJtracTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

//	public void testUserInsertAndLoad() {
//		User user = new User();
//		user.setLoginName("test");
//		user.setEmail("test@jtrac.com");
//		jtrac.storeUser(user);
//		User user1 = jtrac.loadUser("test");
//		assertTrue(user1.getEmail().equals("test@jtrac.com"));
//		User user2 = dao.findUsersByEmail("test@jtrac.com").get(0);
//		assertTrue(user2.getLoginName().equals("test"));
//		//setComplete();
//	}
//
	public void testDeletingUserDeletesItemUsersAlso() {
		System.out.println("1: "+TransactionSynchronizationManager.isActualTransactionActive());
		Space s = getSpace();
		jtrac.storeSpace(s);
		
		User u = new User();
		u.setLoginName("test");
		u.setEmail("dummy");
		u.addSpaceWithRole(s, "DEFAULT");
		jtrac.storeUser(u);
		
		// ========================
		Item i = new Item();
		i.setSpace(s);
		i.setAssignedTo(u);
		i.setLoggedBy(u);
		i.setStatus(State.CLOSED);
		// ========================
		// another user to "watch" this item
		User w = new User();
		w.setLoginName("test1");
		w.setEmail("dummy");
		w.addSpaceWithRole(s, "DEFAULT");
		jtrac.storeUser(w);
		
		ItemUser iu = new ItemUser(w);
		Set<ItemUser> ius = new HashSet<ItemUser>();
		ius.add(iu);
		i.setItemUsers(ius);
		// ========================
		jtrac.storeItem(i, null);
		setComplete();
		endTransaction();
		System.out.println("2: "+TransactionSynchronizationManager.isActualTransactionActive());
		startNewTransaction();
		jtrac.removeUser(w);
		setComplete();
		endTransaction();

		startNewTransaction();
		Item dummyItem = jtrac.loadItem(i.getId());
		assertEquals(0, dummyItem.getItemUsers().size());
		endTransaction();
		cleanDatabase();
		System.out.println("Completed test: testDeletingUserDeletesItemUsersAlso");
		
		//endTransaction();
	}

	private Space getSpace() {
		Space space = new Space();
		space.setPrefixCode("TEST");
		space.setName("Test Space");
		return space;
	}

	private Metadata getMetadata() {
		Metadata metadata = new Metadata();
		String xmlString = "<metadata><fields>" + "<field name='cusInt01' label='Test Label'/>"
				+ "<field name='cusInt02' label='Test Label 2'/>" + "</fields></metadata>";
		metadata.setXmlString(xmlString);
		return metadata;
	}

	private void cleanDatabase() {
		try {
			jdbcTemplate.execute("delete from user_space_roles where id > 1");
			deleteFromTables(new String[] { "history", "items", "spaces", "metadata", "space_sequence" });
			jdbcTemplate.execute("delete from users where id > 1");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	public void testCleanDataBase(){
//		//jtrac.storeSpace(getSpace());
//		cleanDatabase();
//		setComplete();
//	}

}
