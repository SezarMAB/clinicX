# V18 Staff Roles Migration Guide

## Overview
This migration transforms the Staff entity from using a single `role` column to supporting multiple roles via `Set<StaffRole>` using JPA `@ElementCollection`.

## Migration Details

### Files Created
- `V18__migrate_staff_role_to_roles_collection.sql` - Main migration script
- `V18__migrate_staff_role_to_roles_collection_ROLLBACK.sql` - Rollback script
- `V18_MIGRATION_GUIDE.md` - This documentation

### Database Changes
1. **Creates**: `staff_roles` table with `(staff_id, role)` primary key
2. **Migrates**: All existing `staff.role` data to `staff_roles` table
3. **Drops**: `staff.role` column from staff table

## Pre-Migration Checklist

### Environment Verification
- [ ] Verify current Flyway migration status: `./gradlew flywayInfo`
- [ ] Ensure no pending migrations: `./gradlew flywayValidate`
- [ ] Backup database (especially in production environments)
- [ ] Verify application is not running during migration

### Data Verification
- [ ] Query current staff roles:
  ```sql
  SELECT role, COUNT(*) FROM staff GROUP BY role;
  ```
- [ ] Identify staff with NULL or empty roles:
  ```sql
  SELECT id, full_name, email, role FROM staff WHERE role IS NULL OR role = '';
  ```
- [ ] Document current staff count for verification

## Migration Execution

### Local Development (H2)
```bash
SPRING_PROFILES_ACTIVE=h2 ./gradlew flywayMigrate
```

### Local Development (PostgreSQL)
```bash
SPRING_PROFILES_ACTIVE=local ./gradlew flywayMigrate
```

### Production
```bash
SPRING_PROFILES_ACTIVE=postgres ./gradlew flywayMigrate
```

## Post-Migration Verification

### Database Structure Verification
1. **Verify staff_roles table exists**:
   ```sql
   \d staff_roles
   ```

2. **Verify staff.role column is dropped**:
   ```sql
   \d staff
   ```

3. **Verify foreign key constraint**:
   ```sql
   SELECT constraint_name, constraint_type 
   FROM information_schema.table_constraints 
   WHERE table_name = 'staff_roles';
   ```

### Data Integrity Verification
1. **Count migrated roles**:
   ```sql
   SELECT COUNT(*) FROM staff_roles;
   ```

2. **Verify no data loss**:
   ```sql
   SELECT 
       s.id,
       s.full_name,
       s.email,
       sr.role
   FROM staff s
   LEFT JOIN staff_roles sr ON s.id = sr.staff_id
   ORDER BY s.full_name;
   ```

3. **Check for staff without roles**:
   ```sql
   SELECT s.id, s.full_name, s.email 
   FROM staff s 
   LEFT JOIN staff_roles sr ON s.id = sr.staff_id 
   WHERE sr.staff_id IS NULL;
   ```

### Application Testing
1. **Start application**: Verify no startup errors
2. **Staff API endpoints**: Test staff retrieval and role display
3. **Authentication**: Verify role-based access control still works
4. **Staff management**: Test creating/updating staff with multiple roles

## Performance Analysis

### Expected Impact
- **Migration Time**: < 1 second for typical staff table sizes (< 1000 records)
- **Storage Impact**: Minimal increase (one row per staff role)
- **Query Performance**: Improved for role-based queries with proper indexing

### Monitoring Points
- Monitor `staff_roles` table growth
- Watch for N+1 query issues in role fetching (use `@EntityGraph` if needed)
- Verify index usage on `staff_roles.staff_id`

## Environment-Specific Considerations

### Development (H2)
- Migration runs in embedded database
- No special considerations
- Fast execution time

### Local PostgreSQL
- Ensure PostgreSQL is running
- Check connection pool settings
- Monitor for connection timeouts

### Production
- **Backup Strategy**: Full database backup before migration
- **Downtime**: Minimal (< 30 seconds)
- **Rollback Window**: Available immediately after migration
- **Monitoring**: Watch application logs for role-related errors

## Risk Assessment

### Risk Level: **LOW**

### Potential Issues
1. **Application Downtime**: Minimal during migration
2. **Data Loss**: None (all data is preserved)
3. **Performance Impact**: Negligible for small tables

### Mitigation Strategies
1. **Backup**: Complete database backup before migration
2. **Testing**: Thorough testing in development/staging
3. **Rollback**: Rollback script available if issues occur
4. **Monitoring**: Real-time application monitoring post-migration

## Rollback Strategy

### When to Rollback
- Migration fails with data corruption
- Application startup fails due to entity mapping issues
- Critical role-based functionality breaks

### Rollback Execution
```bash
# Execute rollback script manually
psql -d clinicx -f V18__migrate_staff_role_to_roles_collection_ROLLBACK.sql
```

### Post-Rollback Actions
1. Verify staff.role column is restored
2. Check application startup
3. Test staff management functionality
4. Document any data loss (staff with multiple roles)

## Common Issues & Solutions

### Issue: Migration Hangs
**Cause**: Large staff table or database locks
**Solution**: 
- Check for active transactions: `SELECT * FROM pg_stat_activity;`
- Kill blocking queries if necessary
- Retry migration

### Issue: Foreign Key Violation
**Cause**: Orphaned data in staff_roles table
**Solution**:
- Check data integrity before migration
- Clean up orphaned records

### Issue: Application Won't Start
**Cause**: Entity mapping mismatch
**Solution**:
- Verify entity annotations match database schema
- Check for compilation errors
- Review logs for specific error messages

## Testing Checklist

### Pre-Migration Testing
- [ ] Test staff creation with single role
- [ ] Test staff role updates
- [ ] Test role-based access control
- [ ] Document current behavior

### Post-Migration Testing
- [ ] Test staff creation with multiple roles
- [ ] Test staff role updates (add/remove roles)
- [ ] Test role-based access control with multiple roles
- [ ] Test staff list/search functionality
- [ ] Verify API responses include all roles

## Performance Benchmarks

### Expected Query Performance
```sql
-- Before: Direct column access
SELECT id, full_name, role FROM staff WHERE role = 'DOCTOR';

-- After: Join with staff_roles
SELECT s.id, s.full_name, sr.role 
FROM staff s 
JOIN staff_roles sr ON s.id = sr.staff_id 
WHERE sr.role = 'DOCTOR';
```

### Optimization Notes
- Index on `staff_roles(staff_id)` created for fast lookups
- Consider `staff_roles(role)` index if role-based queries are frequent
- Use `@EntityGraph` for eager loading of roles when needed

---

**Migration Created**: 2025-01-14  
**Estimated Execution Time**: < 30 seconds  
**Rollback Available**: Yes  
**Production Ready**: Yes