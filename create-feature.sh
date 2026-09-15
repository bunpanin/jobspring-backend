#!/bin/bash

# ============================================================
# Spring Boot Feature Generator
#
# Usage:
# ./scripts/create-feature.sh JobLevel
# ./scripts/create-feature.sh Department
# ./scripts/create-feature.sh Candidate
# ============================================================

if [ -z "$1" ]; then
    echo "❌ Please provide feature name."
    echo "Example:"
    echo "./scripts/create-feature.sh JobLevel"
    exit 1
fi

FEATURE_NAME="$1"
FEATURE_PACKAGE="$FEATURE_NAME"

BASE_PACKAGE="jobspring_backend"

BASE_PATH="src/main/java/$(echo "$BASE_PACKAGE" | tr '.' '/')"

FEATURE_PATH="$BASE_PATH/features/$FEATURE_PACKAGE"

echo "🚀 Creating feature: $FEATURE_NAME"
echo "📁 Location: $FEATURE_PATH"

# ============================================================
# Create folders
# ============================================================

mkdir -p "$FEATURE_PATH/dto"
mkdir -p "$FEATURE_PATH/dto/requests"
mkdir -p "$FEATURE_PATH/dto/responses"
mkdir -p "$FEATURE_PATH/entity"

# ============================================================
# Entity
# ============================================================

cat > "$FEATURE_PATH/entity/${FEATURE_NAME}.java" <<EOF
package ${BASE_PACKAGE}.features.${FEATURE_PACKAGE}.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "${FEATURE_PACKAGE}")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ${FEATURE_NAME} {

}
EOF

# ============================================================
# Repository
# ============================================================

cat > "$FEATURE_PATH/${FEATURE_NAME}Repository.java" <<EOF
package ${BASE_PACKAGE}.features.${FEATURE_PACKAGE};

import ${BASE_PACKAGE}.features.${FEATURE_PACKAGE}.entity.${FEATURE_NAME};
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ${FEATURE_NAME}Repository extends JpaRepository<${FEATURE_NAME}, Long> {

}
EOF

# ============================================================
# Service
# ============================================================

cat > "$FEATURE_PATH/${FEATURE_NAME}Service.java" <<EOF
package ${BASE_PACKAGE}.features.${FEATURE_PACKAGE};
public interface ${FEATURE_NAME}Service {

}
EOF

# ============================================================
# Service Implementation
# ============================================================

cat > "$FEATURE_PATH/${FEATURE_NAME}ServiceImpl.java" <<EOF
package ${BASE_PACKAGE}.features.${FEATURE_PACKAGE};
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ${FEATURE_NAME}ServiceImpl implements ${FEATURE_NAME}Service {

}
EOF

# ============================================================
# Controller
# ============================================================

cat > "$FEATURE_PATH/${FEATURE_NAME}Controller.java" <<EOF
package ${BASE_PACKAGE}.features.${FEATURE_PACKAGE};
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/${FEATURE_PACKAGE}")
@RequiredArgsConstructor
public class ${FEATURE_NAME}Controller {

    private final ${FEATURE_NAME}Service service;

}
EOF

echo ""
echo "✅ Feature '$FEATURE_NAME' created successfully!"
echo ""
echo "Generated:"
echo " ├── dto"
echo " │   ├── requests"
echo " │   │   └── ${FEATURE_NAME}Request.java"
echo " │   └── responses"
echo " │       └── ${FEATURE_NAME}Response.java"
echo " ├── entity"
echo " │   └── ${FEATURE_NAME}.java"
echo " ├── ${FEATURE_NAME}Controller.java"
echo " ├── ${FEATURE_NAME}Repository.java"
echo " ├── ${FEATURE_NAME}Service.java"
echo " └── ${FEATURE_NAME}ServiceImpl.java"
echo ""

if git rev-parse --is-inside-work-tree > /dev/null 2>&1; then
    git add "$FEATURE_PATH"
    echo "✅ Added generated files to Git staging"
fi