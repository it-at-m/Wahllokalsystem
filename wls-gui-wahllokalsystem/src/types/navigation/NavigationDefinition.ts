export interface NavigationDefinition<T> {
  title: string;
  targetRouteName: T;
  disabled: boolean; //TODO Optional für false ergänzen
}
