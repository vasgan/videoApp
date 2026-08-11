/*
package com.example.myapplication

// Задача: По клику на кнопку, должно отсчитаться 5 секунд
// и текст "hello world" должен измениться на "goodbye world".
// Напиши как ты обычно решаешь такую задачу у себя на продакшн проекте.
// В рамках задачи можно использовать любые библиотеки и подходы.

@Composable
fun TextChangeScreen() {
    val text = viewModel.uiState.collectAsState()

    Button(onClick = {
        viewModel.onClick()
    }) {
        Text(text.text)
    }
}

fun TextChangeViewModel : ViewModel(){

    private _uiState = mutableStateFlow()
    val uiState : StateFLow<textState> = _uiState.toStateFlow()
    val timerState : Job? = null

    init {
        _uiState.value = hello "hello world"
    }

    fun changeText(timeClick: Int) {
        if(_uiState.value is goodbye) return;
        if (timerState != null)
            timerState.cancel()
        timerState = viewModelScope.launch {
            delay(5000L)
            _uiState.value = "goodbye world"
        }
    }
}

sealed class textState(text)

hello
goodbye(te)

// ******************************************


// Проведи рефакторинг кода.
// Сконцентрируйся на его работоспособности, верстку считаем правильной.

data class Product(
    val id: Int,
    val name: String, //todo
    var isSelected: Boolean
)
@Composable
fun ProductListScreen(
    products: List<Product>,
    onProductsChange: (List<Product>) -> Unit
) {
    val listState = LazyListState()
    val scope = remeberCorutineScope()

    var selectedCount by remember { mutableStateOf(0) }
    var showScrollToTop by remember { mutableStateOf(false) }

    Column {
        Text(text = "Выбрано: $selectedCount")

        LazyColumn(state = listState) {
            itemsIndexed(
                items = products
            ) { product: Product, index: Int ->
                val onCheckedChange = { isChecked: Boolean ->
                    product.isSelected = isChecked
                    selectedCount = products.count { it.isSelected }
                    onProductsChange(products)
                }
                ProductItem(
                    product = product,
                    onSelectionChanged = onCheckedChange
                )
            }
        }
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        showScrollToTop = listState.firstVisibleItemIndex > 5
    }

    if (showScrollToTop) {
        FloatingActionButton(
            onClick = {
                scope(Dispatchers.Default).launch {
                    listState.animateScrollToItem(0)
                }
            },
        ) {
            Text(text = "Наверх")
        }
    }
}

@Composable
fun ProductItem(product: Product, onSelectionChanged: (Boolean) -> Unit)


// ****************************************************


// У нас есть кошелек, мы можем его пополнять, выводить с него деньги, либо переводить их на другой кошелек.
// Баланс может уходить в минус.
//
// Задание 1: Что будет выведено в консоль?
// Задание 2: Проведи рефакторинг кода

class WBWallet(initialBalance: Int, val owner: String) {
    var balance: Int = initialBalance
    val history: MutableList<Transaction> = mutableListOf()

    fun deposit(amount: Int) {
        balance += amount
        history.add(Transaction("+$amount"))
    }

    fun withdraw(amount: Int) {
        balance -= amount
        history.add(Transaction("-$amount"))
    }

    fun transferTo(otherWallet: WBWallet, amount: Int) {
        synchronized(this) {
            balance -= amount
            history.add(Transaction("-$amount"))
        }

        synchronized(otherWallet) {
            otherWallet.balance += amount
            otherWallet.history.add(Trnsaction("+$amount"))
        }

    }
}

data class Transaction(
    val amount: String // "+10", "-10"
)

// *********************************
// Задача: провести рефакторинг кода.

class BaseError: BaseAnswer

class UserUseCase(@Inject repository: UserRepository) {


    fun registerUser(username: String, email: String): Flow<BaseAnswer<String>> {
        flow {
            if (isValidUsername(username) && isValidEmail(email)) {
                val user = User(username, email)// вот из нижних двух строк мы бы сделали мап флоу в один и возвращали его
                repository.saveUserToDatabase(user) // репозитрий
                repository.sendEmailConfirmation(email) // репозиторий
            }else {
                BaseError("Invalid username or email.")
            }
        }

        private fun isValidUsername(username: String): Boolean {
            // Validation logic for username
            return username.isNotEmpty()
        }

        private fun isValidEmail(email: String): Boolean {
            // Validation logic for email
            return email.contains("@")
        }

    }
    interface UserRepository{
        override fun saveUserToDB(user: User): Flow
        override fun sendUserCoinformation(user: User): Flow
    }

    class UserRepositoryImpl() : UserRopisoty

    override fun saveUserToDB(user: User): Flow {
        return flow{}
    }

    override fun sendUserCoinformation(user: User): Flow {
        return flow{}
    }

    data class User(val username: String, val email: String)
*/
