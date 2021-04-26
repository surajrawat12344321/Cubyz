template<class T>
class Vector2{
	public:
	Vector2(T x,T y){
		this->x=x;
		this->y=y;
	}
	T x;
	T y;
	Vector2<T> operator+(Vector2<T> b){
		return Vector2<T>(x+b.x,y+b.y); 
	}
	Vector2<T> operator-(Vector2<T> b){
		return Vector2<T>(x-b.x,y-b.y); 
	}
	T operator*(Vector2<T> b){
		return x*b.x+y*b.y; 
	}
};
template<class T>
class Vector3{
	public:
	Vector3(T x,T y,T z){
		this->x=x;
		this->y=y;
		this->z=z;	
	}

	T x;
	T y;
	T z;

	Vector3<T> operator+(Vector3<T> b){
		return Vector3<T>(x+b.x,y+b.y,z+b.z); 
	}
	Vector3<T> operator-(Vector3<T> b){
		return Vector3<T>(x-b.x,y-b.y,z-b.z); 
	}
	T operator*(Vector3<T> b){
		return x*b.x+y*b.y+z*b.z; 
	}
};

typedef Vector2<float> Vector2f;
typedef Vector2<int> Vector2i;
typedef Vector2<unsigned int> Vector2u;
typedef Vector2<long> Vector2l;

typedef Vector3<float> Vector3f;
typedef Vector3<int> Vector3i;
typedef Vector3<unsigned int> Vector3u;
typedef Vector3<long> Vector3l;
