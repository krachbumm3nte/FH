package kw43;

import java.util.function.*;

public abstract class Result<V> {
	@SuppressWarnings("rawtypes")
	private static Result empty = new Empty();

	public abstract V getOrElse(final V defaultValue);

	public abstract V getOrElse(final Supplier<V> defaultValue);

	public abstract <U> Result<U> map(Function<V, U> f);

	public abstract <U> Result<U> flatMap(Function<V, Result<U>> f);

	public abstract boolean isSuccess();

	private Result() {
	}

	private static class Failure<V> extends Result<V> {
		private final RuntimeException exception;

		private Failure(String message) {
			super();
			this.exception = new IllegalStateException(message);
		}

		private Failure(RuntimeException e) {
			super();
			this.exception = e;
		}

		private Failure(Exception e) {
			super();
			this.exception = new IllegalStateException(e.getMessage(), e);
		}

		@Override
		public String toString() {
			return String.format("Failure(%s)", exception.getMessage());
		}

		public V getOrElse(V defaultValue) {
			return defaultValue;
		}

		public V getOrElse(Supplier<V> defaultValue) {
			return defaultValue.get();
		}

		public <U> Result<U> map(Function<V, U> f) {
			return failure(exception);
		}

		public <U> Result<U> flatMap(Function<V, Result<U>> f) {
			return failure(exception);
		}

		@Override
		public boolean isSuccess() {
			return false;
		}

	}

	private static class Success<V> extends Result<V> {
		private final V value;

		private Success(V value) {
			super();
			this.value = value;
		}

		private V successValue() {
			return this.value;
		}

		@Override
		public String toString() {
			return String.format("Success(%s)", value.toString());
		}

		public V getOrElse(V defaultValue) {
			return value;
		}

		public V getOrElse(Supplier<V> defaultValue) {
			return value;
		}

		public <U> Result<U> map(Function<V, U> f) {
			try {
				return success(f.apply(successValue()));
			} catch (Exception e) {
				return failure(e);
			}
		}

		public <U> Result<U> flatMap(Function<V, Result<U>> f) {
			try {
				return f.apply(successValue());
			} catch (Exception e) {
				return failure(e.getMessage());
			}
		}

		@Override
		public boolean isSuccess() {
			return true;
		}
	}

	private static class Empty<V> extends Result<V> {
		public Empty() {
			super();
		}

		@Override
		public V getOrElse(final V defaultValue) {
			return defaultValue;
		}

		@Override
		public <U> Result<U> map(Function<V, U> f) {
			return empty();
		}

		@Override
		public <U> Result<U> flatMap(Function<V, Result<U>> f) {
			return empty();
		}

		@Override
		public String toString() {
			return "Empty()";
		}

		@Override
		public V getOrElse(Supplier<V> defaultValue) {
			return defaultValue.get();
		}

		@Override
		public boolean isSuccess() {
			return false;
		}
	}

	public static <V> Result<V> failure(String message) {
		return new Failure<>(message);
	}

	public static <V> Result<V> failure(Exception e) {
		return new Failure<V>(e);
	}

	public static <V> Result<V> failure(RuntimeException e) {
		return new Failure<V>(e);
	}

	public static <V> Result<V> success(V value) {
		return new Success<>(value);
	}

//	public Result<V> orElse(Supplier<Result<V>> defaultValue) {
//		return map(x -> this).getOrElse(defaultValue);
//	}

	@SuppressWarnings("unchecked")
	public static <V> Result<V> empty() {
		return empty;
	}

}
