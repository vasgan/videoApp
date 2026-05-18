package com.example.myapplication.test
/*

import androidx.compose.runtime.traceEventStart
import kotlin.math.abs

class Solution {
    fun lengthOfLongestSubstring(s: String): Int {
        var result: String = s[0].toString()
        var maxLengthlength = 0
        var startI = 0
        for (i in 0..<s.length) {
            for (j in 0..<result.length) {
                if (s[i] == result[j]) {
                    maxLengthlength = result.length
                    i = startI++
                }
            }
        }
        return result.length
    }
    fun maxArea(height: IntArray): Int {
        var maxS = 0
        for (i in 0 ..<height.size) {
            for (j in i..<height.size) {
                val s =  getMin(height[i], height[j]) * (i - j)
                if (s > maxS)
                    maxS = s
            }
        }
        return maxS
    }

    fun getMin(n : Int, m : Int): Int {
        return if (n < m) n else m
    }

    fun threeSumClosest(nums: IntArray, target: Int): Int {
        nums.sort()
        var closedSum = nums[0] + nums[1] + nums[2]
        for (i in 0..nums.size - 2 ) {
            var left = nums[i + 1]
            var right = nums.size - 1
            while (left < right) {
                val currentSum = nums[i] + nums[left] + nums[right]
                if (abs(closedSum - target) > abs(currentSum - target)) {
                    closedSum = currentSum
                }
                when {
                    currentSum < target -> left ++
                    currentSum > target -> right--
                    currentSum == target -> return currentSum
                }
            }
        }
        return closedSum
    }

    class ListNode(var `val`: Int) {
         var next: ListNode? = null
    }
    fun removeNthFromEnd(head: ListNode?, n: Int): ListNode? {
        if (head == null) return null
        var myHead = head
        var deleteHead = head
        var i = 0
        while (myHead?.next != null) {
            myHead = myHead.next
            i++
            if (i > n)
                deleteHead = deleteHead?.next
        }
        deleteHead?.next = deleteHead.next?.next
        if (i == n) return deleteHead
        return head
    }

    fun swapPairs(head: ListNode?): ListNode? {
        val dummy = ListNode(0)
        val prev = head
        while (prev?.next != null && prev.next?.next != null) {
            val first
        }
        return dummy.next
    }
    fun swap(first: ListNode, second: ListNode): ListNode {
        val tmp = second.next
        second.next = first.next
        first.next = tmp
        return second
    }

    fun divide(dividend: Int, divisor: Int): Int {
        var minus = false
        if (dividend < 0 || divisor < 0) {
            minus = true
        }
        if (dividend < 0 && divisor < 0)
            minus = false
        if (divisor == 0) return 0
        if (abs(divisor) == 1) return if(minus) -abs(dividend) else abs(dividend)
        if (divisor == dividend) return if(minus) -1 else 1
        var count = 0
        var summ = 0
        while (summ + abs(divisor) <= abs(dividend)) {
            count++
            summ += abs(divisor)
        }
        if (minus) return -count
        return count
    }

    fun searchRange(nums: IntArray, target: Int): IntArray {
        if (nums.size == 0) return
        if (target < nums[0]) return
        if (target > nums[nums.size -1 ]) return
        val result = IntArray(2)
        var leftIn = 0
        var rightIn = nums.size
        while (leftIn <= rightIn) {
            val mid: Int = leftIn + (rightIn - leftIn) / 2
            if (nums[mid] == target) {
                return
            }
            else {
                //поднимаем left
                if (target > nums[mid]) {
                    leftIn = mid + 1
                } else {//опускаем right
                    rightIn = mid - 1
                }
            }
        }

        return IntArray(-1,{1,-1})
    }
}*/

// Задача: По клику на кнопку, должно отсчитаться 5 секунд
// и текст "hello world" должен измениться на "goodbye world".
// Напиши как ты обычно решаешь такую задачу у себя на продакшн проекте.
// В рамках задачи можно использовать любые библиотеки и подходы.
/*

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




/*class WBWallet(initialBalance: Int, val owner: String) {
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
}*/













