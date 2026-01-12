#!/bin/bash

# Script de inicio rápido para Lubricentro Backend con Docker
# Autor: Emilio Heguy
# Fecha: $(date +%Y-%m-%d)

set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para imprimir mensajes con colores
print_message() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE}  Lubricentro Backend Docker${NC}"
    echo -e "${BLUE}================================${NC}"
}

# Función para verificar si Docker está ejecutándose
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker no está ejecutándose. Por favor, inicia Docker Desktop o el daemon de Docker."
        exit 1
    fi
    print_message "Docker está ejecutándose correctamente."
}

# Función para verificar si Docker Compose está disponible
check_docker_compose() {
    if ! docker-compose --version > /dev/null 2>&1; then
        print_error "Docker Compose no está disponible. Por favor, instálalo."
        exit 1
    fi
    print_message "Docker Compose está disponible."
}

# Función para verificar si los puertos están disponibles
check_ports() {
    local backend_port=8080
    local db_port=5432
    
    if lsof -Pi :$backend_port -sTCP:LISTEN -t >/dev/null 2>&1; then
        print_warning "Puerto $backend_port está ocupado. La aplicación podría no iniciarse correctamente."
    fi
    
    if lsof -Pi :$db_port -sTCP:LISTEN -t >/dev/null 2>&1; then
        print_warning "Puerto $db_port está ocupado. La base de datos podría no iniciarse correctamente."
    fi
}

# Función para dar permisos al Maven wrapper
fix_permissions() {
    if [ -f "mvnw" ]; then
        chmod +x mvnw
        print_message "Permisos del Maven wrapper corregidos."
    fi
}

# Función para limpiar volúmenes de PostgreSQL
clean_volumes() {
    print_warning "Limpiando volúmenes de PostgreSQL..."
    docker-compose down -v
    print_message "Volúmenes eliminados. La base de datos se recreará al iniciar."
}

# Función para verificar y corregir problemas de PostgreSQL
check_postgres_health() {
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        # Verificar si el contenedor está corriendo
        if ! docker-compose ps postgres | grep -q "Up"; then
            # Contenedor no está corriendo, verificar logs para errores conocidos
            local logs_output
            logs_output=$(docker-compose logs postgres 2>&1 | tail -20)
            
            # Verificar error de incompatibilidad de versiones
            if echo "$logs_output" | grep -q "database files are incompatible"; then
                print_error "Error detectado: Los datos de PostgreSQL son incompatibles con la versión actual."
                print_warning "Esto ocurre cuando se actualiza la versión de PostgreSQL."
                echo ""
                print_message "Limpiando volúmenes para recrear la base de datos con la nueva versión..."
                clean_volumes
                print_message "Reiniciando servicios..."
                docker-compose up -d --build
                attempt=1
                continue
            fi
            
            # Verificar error de rol que no existe
            if echo "$logs_output" | grep -q "role.*does not exist"; then
                print_error "Error detectado: el rol 'postgres' no existe en PostgreSQL."
                print_warning "Esto generalmente ocurre por datos antiguos en el volumen persistente."
                echo ""
                print_message "Limpiando volúmenes para recrear la base de datos..."
                clean_volumes
                print_message "Reiniciando servicios..."
                docker-compose up -d --build
                attempt=1
                continue
            fi
        fi
        
        # Intentar verificar si PostgreSQL está listo
        if docker-compose exec -T postgres pg_isready -U postgres -d lubricentro_db > /dev/null 2>&1; then
            print_message "PostgreSQL está listo."
            return 0
        fi
        
        if [ $attempt -eq $max_attempts ]; then
            print_error "PostgreSQL no se pudo iniciar en el tiempo esperado."
            print_message "Últimos logs de PostgreSQL:"
            docker-compose logs --tail=50 postgres
            echo ""
            print_warning "Si el error persiste, intenta limpiar los volúmenes:"
            print_message "  docker-compose down -v"
            print_message "  ./start.sh"
            exit 1
        fi
        
        print_message "Esperando a que PostgreSQL esté listo... (intento $attempt/$max_attempts)"
        sleep 2
        attempt=$((attempt + 1))
    done
}

# Función para iniciar los servicios
start_services() {
    print_message "Iniciando servicios con Docker Compose..."
    
    # Construir e iniciar en segundo plano
    docker-compose up -d --build
    
    print_message "Servicios iniciados. Esperando a que estén listos..."
    
    # Verificar salud de PostgreSQL
    check_postgres_health
    
    # Esperar a que el backend esté listo
    local max_attempts=30
    local attempt=1
    while [ $attempt -le $max_attempts ]; do
        if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1 || curl -s http://localhost:8080 > /dev/null 2>&1; then
            print_message "Backend está listo."
            break
        fi
        
        if [ $attempt -eq $max_attempts ]; then
            print_error "Backend no se pudo iniciar en el tiempo esperado."
            docker-compose logs backend
            exit 1
        fi
        
        print_message "Esperando a que el backend esté listo... (intento $attempt/$max_attempts)"
        sleep 3
        attempt=$((attempt + 1))
    done
}

# Función para mostrar el estado de los servicios
show_status() {
    print_message "Estado de los servicios:"
    docker-compose ps
    
    echo ""
    print_message "URLs de acceso:"
    echo -e "  ${BLUE}Backend API:${NC} http://localhost:8080"
    echo -e "  ${BLUE}Swagger UI:${NC} http://localhost:8080/swagger-ui.html"
    echo -e "  ${BLUE}Base de datos:${NC} localhost:5432"
    
    echo ""
    print_message "Comandos útiles:"
    echo -e "  ${BLUE}Ver logs:${NC} docker-compose logs -f"
    echo -e "  ${BLUE}Detener:${NC} docker-compose down"
    echo -e "  ${BLUE}Reiniciar:${NC} docker-compose restart"
    echo -e "  ${BLUE}Limpiar volúmenes:${NC} ./start.sh --clean"
}

# Función principal
main() {
    # Procesar argumentos de línea de comandos
    case "${1:-}" in
        --clean|--reset)
            print_header
            print_message "Limpiando volúmenes y reiniciando servicios..."
            check_docker
            check_docker_compose
            clean_volumes
            print_message "Iniciando servicios..."
            start_services
            echo ""
            show_status
            echo ""
            print_message "¡Lubricentro Backend está ejecutándose correctamente! 🎉"
            return 0
            ;;
        --help|-h)
            echo "Uso: ./start.sh [opciones]"
            echo ""
            echo "Opciones:"
            echo "  --clean, --reset    Limpia los volúmenes de PostgreSQL y reinicia los servicios"
            echo "  --help, -h          Muestra esta ayuda"
            echo ""
            echo "Sin opciones, inicia los servicios normalmente."
            return 0
            ;;
        "")
            # Sin argumentos, comportamiento normal
            ;;
        *)
            print_error "Opción desconocida: $1"
            print_message "Usa --help para ver las opciones disponibles"
            return 1
            ;;
    esac
    
    print_header
    
    print_message "Verificando prerrequisitos..."
    check_docker
    check_docker_compose
    check_ports
    fix_permissions
    
    print_message "Iniciando Lubricentro Backend..."
    start_services
    
    echo ""
    show_status
    
    echo ""
    print_message "¡Lubricentro Backend está ejecutándose correctamente! 🎉"
    print_message "Puedes acceder a la API en http://localhost:8080"
}

# Función para limpiar al salir
cleanup() {
    print_message "Limpiando..."
    docker-compose down
    print_message "Servicios detenidos."
}

# Capturar señal de interrupción (Ctrl+C)
trap cleanup INT

# Ejecutar función principal
main "$@"
