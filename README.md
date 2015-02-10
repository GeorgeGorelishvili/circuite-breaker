Circuit Breaker მოდული  

* maven dependency:  

  groupId: com.azry  
  artifactId: circuit-breaker  
  version: 1.0  

* აღწერა  
  
  მოდულის დანიშნულებაა დროულად მოხდეს რომელიღაც გარე რესურსის დროებითი დეაქტივაცია, რაც არასტაბილური გარე სერვისის (რესურსის) შემთხვევაში აგვარიდებს თავიდან  მთელი სისტემის შენელება-შეფერხებას.


* ინიციალიზაცია
  
თითოეული გარე სერვისი (რესურსი) შესაძლებელია წინასწარ დავარეგისტრიროთ უნიკალური სიტყვა-გასაღებით. მაგ:
  
``` java
		new CircuitBreakerManager.Builder(SERVICE_UNIQUE_KEY) 
			.maxExecTime(MAX_EXECUTION_TIME)
			.openStateTimeout(OPEN_STATE_TIMEOUT)
			.maxTimeoutFail(MAX_TIMEOUT_FAIL)
			.get(); 
```

 ```SERVICE_UNIQUE_KEY``` - სერვისისთვის უნიკალური კონფიგურაციის სახელი  
 ```MAX_EXECUTION_TIME``` - სერვისის გამოძახება-პასუხის მაქსიმალური დრო (მილიწამი, default: 10 წამი)  
 ```OPEN_STATE_TIMEOUT``` -  სერვისის დეაქტივირებული რეჟიმიდან გამოსლის დრო (მილიწამი, default: 30 წუთი)  
 ```MAX_TIMEOUT_FAIL``` - ჩავარდნილი გამოძახებების რაოდენობა, რომლის მიღწევის შემთხვევაში კონფიგურაცია გადადის დეაქტივირებულ რეჟიმში (default: 3)   

default პარამეტრებით ინიციალიზაცია:  
``` java
		new CircuitBreakerManager.Builder(SERVICE_UNIQUE_KEY).get(); 
```  

  
  საჭიროების შემთხვევაში უნიკალური ```(SERVICE_UNIQUE_KEY)``` სიტყვა-გასაღებით   ```CircuitBreakerManager```-იდან მოვითხოვთ დარეგისტრირებულ წინასწარ დაკონფიგურირებულ ```Circuit Breaker```-ს. თუ ასეთი ობიქტი არ არსებობს მაშინ შეიქმნება ახალი default პარამეტრების ან გადაცემული პარამეტრების საფუძველზე.
  
  

* გამოყენება  

სერვისის გამოძახების დროს, მენეჯერიდან ვიღებთ ```Circuit Breaker``` ობიექტს უნიკალური გასაღებით. მაგ:
``` java
		CircuitBreaker circuitBreaker = new CircuitBreakerManager.Builder(SERVICE_UNIQUE_KEY).get();
				try {
					if (circuitBreaker.canCall()) {
						... // სერვისის გამოძახების ლოგიკა
					}
				} catch (Exception ex) {
					...
				} finally {
					circuitBreaker.endCall();
				}
```  

```circuitBreaker ``` ცვლადს აქვს ინფორმაცია ამ სერვისის წინა გამოძახებების შესახებ და ``` circuitBreaker.canCall() ``` სერვისი ხელმისაწვდომია თუ არა. ``` circuitBreaker.endCall() ``` გამოძახების შემთხვევაში Circuit Breaker-ს ვეუბნებით რომ გამოძახება დასრულდა და შიდა ლოგიკა ახდენს მიმდინარე state-ის განახლებას.

  
  
* StateChangeCallback  
  

გვაქვს საშუალება კოდის ფრაგმენტის შესრულების, callback-ში გვიბრუნდება ახალი state და იძახება იმ შემთხვევაში თუ state შეიცვალა. 
``` java
		CircuitBreaker circuitBreaker = new CircuitBreakerManager.Builder(SERVICE_UNIQUE_KEY)
				.callback(new StateChangeCallback() {
					@Override
					public void onStateChange(CircuitBreakerState state) {
						if (state == CircuitBreakerState.CLOSED) {
							... 
						} 
					}
				}).get();
```

```CircuitBreaker``` კონფიგურაციის state:

``` java
		public enum CircuitBreakerState {
			OPEN, // სერვისი დეაქტივირებულია
			HALF_OPEN, // დეაქტივაციის რეჯიმიდან გამოსვლა, სერვისის გადამოწმება
			CLOSED // სერვისი აქტიურია
		}
```
  

* პარამეტრების runtime-ში შეცვლა:  
გვაქვს საშუალება სერვისის გამოძახების წინ შეუცვალოთ პარამეტრები, რომელიც შეიცვლება მხოლოდ მომდინარე სრედისთვის. როდესაც სერვისის გამოძახება დასრულდება ```Circuit Breaker``` ავტომატურად ანახლებს state-ს, როდესაც სხვა სრედი მოითხოვს მას უკვე განახლებული state დახვდება.
``` .disableConfigurationUpdate() ``` მეთოდის გამოძახების შემთხვევაში გლობალურად არ მოხდება კონფიგურაციის პარამეტრების შეცვლა.

ქვემოთ მოყვენილი ფრაგმენტის შემთხვევაში გამომძახებელი სრედში განახლებული პარამეტრით იმუშავებს, ხოლო მომდევნო გამომძახებელ სრედს დახვდება ძველი კონფიგურაცია


``` java
		CircuitBreaker circuitBreaker = new CircuitBreakerManager.Builder(SERVICE_UNIQUE_KEY) 
			.maxExecTime(MAX_EXECUTION_TIME)
			.disableConfigurationUpdate()
			.get(); 
```
