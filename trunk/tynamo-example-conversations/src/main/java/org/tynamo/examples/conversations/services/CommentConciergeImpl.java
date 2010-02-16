package org.tynamo.examples.conversations.services;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.servlet.http.HttpServletRequest;

import org.tynamo.conversations.services.Conversation;
import org.tynamo.conversations.services.ConversationManager;

public class CommentConciergeImpl implements CommentConcierge {
	private static final int COMMENTLIST_SIZE = 10;
	String[] comments = new String[COMMENTLIST_SIZE];

	// Cache is mostly needed for GAE but using it elsewhere doesn't hurt
	private Cache commentCache;

	Set<Integer> openCommentSpots = Collections.synchronizedSet(new LinkedHashSet<Integer>(COMMENTLIST_SIZE));
	Map<String, Reservation> spotReservations = Collections.synchronizedMap(new HashMap<String, Reservation>(100));

	// Only needed for threadedless environments such as GAE
	Map<Long, Reservation> scheduledReservations = Collections.synchronizedMap(new HashMap<Long, Reservation>(100));
	HttpServletRequest request;
	ConversationManager conversationManager;

	public CommentConciergeImpl(HttpServletRequest request, ConversationManager conversationManager) {
		this.request = request;
		this.conversationManager = conversationManager;
		for (int i = 0; i < COMMENTLIST_SIZE; i++)
			openCommentSpots.add(i);

		try {
			CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
			commentCache = cacheFactory.createCache(Collections.emptyMap());
		} catch (CacheException e) {
		} catch (Exception e) {
		}
	}

	public String[] getComments() {
		// It's enough to examine the first item - presumably, if it's null, they are all null
		if (comments[0] == null && commentCache != null) for (int i = 0; i < 10; i++)
			comments[i] = (String) commentCache.get(i);
		return comments;
	}

	@SuppressWarnings("unchecked")
	/**
	 * Tries to reserve a comment spot. Conversation must have been opened before for
	 * calling this operation
	 */
	public Integer reserveCommentSpot() {
		String sessionId = request.getSession(false).getId();
		freeExistingReservation(sessionId);
		if (openCommentSpots.size() <= 0) return null;

		Integer spot = null;
		Iterator<Integer> iterator = openCommentSpots.iterator();
		while (iterator.hasNext()) {
			spot = iterator.next();
			try {
				openCommentSpots.remove(spot);
				break;
			} catch (ConcurrentModificationException e) {
				spot = null;
			}
		}
		if (spot == null) return null;
		Reservation reservation = new Reservation(sessionId, spot);
		spotReservations.put(sessionId, reservation);

		// The try-catch is a horrible hack to make things work in environments where creating thread isn't allowed,
		// such as GAE. Don't use this as an example of best practices
		try {
			Executors.newSingleThreadScheduledExecutor().schedule(reservation, 61, TimeUnit.SECONDS);
		} catch (Exception e) {
			scheduledReservations.put(System.currentTimeMillis() + 61 * 1000L, reservation);
		}
		return spot;
	}

	// A
	public void cleanScheduledReservations() {
		Set<Entry<Long, Reservation>> set = scheduledReservations.entrySet();
		try {
			// Wonder if this works or should I use an iterator
			for (Entry<Long, Reservation> entry : set)
				if (entry.getKey() > System.currentTimeMillis()) set.remove(entry);
		} catch (Exception e) {
		}
	}

	public boolean setComment(String comment) {
		if (comment == null || comment.trim().isEmpty()) return false;
		String conversationId = conversationManager.getActiveConversation();
		if (conversationId == null) return false;
		String sessionId = request.getSession(false).getId();
		Reservation reservation = spotReservations.get(sessionId);
		if (reservation == null) return false;
		comments[reservation.spot] = comment;
		if (commentCache != null) commentCache.put(reservation.spot, comment);
		return true;
	}

	private void freeExistingReservation(String sessionId) {
		Reservation reservation = spotReservations.get(sessionId);
		if (reservation != null) reservation.free();
	}

	public void onConversationCreated(Conversation conversation) {
	}

	public void onConversationEnded(Conversation conversation, boolean expired) {
		freeExistingReservation(conversation.getSessionId());
	}

	@SuppressWarnings("unchecked")
	class Reservation implements Callable {
		String sessionId;
		Integer spot;

		Reservation(String sessionId, Integer spot) {
			this.sessionId = sessionId;
			this.spot = spot;
		}

		public Object call() throws Exception {
			return free();
		}

		public boolean free() {
			if (sessionId == null) return false;
			if (equals(spotReservations.get(sessionId))) spotReservations.remove(sessionId);
			sessionId = null;
			openCommentSpots.add(spot);
			return true;
		}
	}
}
